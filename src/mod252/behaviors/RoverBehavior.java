package mod252.behaviors;

import jade.core.*;

import jade.core.behaviours.*;

import jade.lang.acl.ACLMessage;

import jess.*;

import java.io.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class RoverBehavior extends CyclicBehaviour {

	// class variables
	Rete jess; // holds the pointer to jess
	Agent myAgent; // holds the pointer to this agent
	int m_maxJessPasses = 0; // holds the maximum number of Jess passes for each
								// run
	int executedPasses = -1; // to count the number of Jess passes in the
								// previous run
	Hashtable AIDCache; // holds a local cache to map agent names to AID

	public RoverBehavior(Agent agent, String jessFile) {
		myAgent = agent;
		AIDCache = new Hashtable();

		jess = new Rete();

		try {
			// First I define the ACLMessage template
			jess.executeCommand(ACLJessTemplate());

			// Then I define the myagent template
			jess.executeCommand("(deftemplate MyAgent (slot name))");

			// Then I add the send function
			jess.addUserfunction(new JessSend(myAgent, this));

			// Then I assert the fact (Myagent (name <my-name>))
			jess.executeCommand("(deffacts MyAgent \"All facts about this agent\" (MyAgent (name "
					+ myAgent.getName() + ")))");

			// Open the file test.clp
			FileReader fr = new FileReader(jessFile);

			// Create a parser for the file, telling it where to take input
			// from and which engine to send the results to
			Jesp j = new Jesp(fr, jess);

			// parse and execute one construct, without printing a prompt
			j.parse(false);
		} catch (JessException re) {
			System.out.println(re);
		} catch (FileNotFoundException e) {
			System.out.println(e);
		}
	}

	public RoverBehavior(Agent agent, String jessFile, int maxJessPasses) {
		this(agent, jessFile);
		m_maxJessPasses = maxJessPasses;
	}

	/**
	 * executes the behaviour
	 */
	public void action() {
		ACLMessage msg; // to keep the ACLMessage

		// wait a message
		if (executedPasses < m_maxJessPasses) {
			System.out.println(myAgent.getName()
					+ " is blocked to wait a message...");
			msg = myAgent.blockingReceive();

			// assert the fact message in Jess
			makeassert(ACL2JessString(msg));
		} else {
			System.out.println(myAgent.getName()
					+ " is checking if there is a message...");
			msg = myAgent.receive();

			if (msg != null) {
				// assert the fact message in Jess
				makeassert(ACL2JessString(msg));
			}
		}

		// run jess
		try {
			// jess.executeCommand("(facts)");
			if (m_maxJessPasses > 0) {
				executedPasses = jess.run(m_maxJessPasses);
				System.out.println("Jess has executed " + executedPasses
						+ " passes");
			} else {
				jess.run();
			}
		} catch (JessException re) {
			re.printStackTrace(System.err);
		}
	}

	private boolean isEmpty(String string) {
		return (string == null) || string.equals("");
	}

	/**
	 * replace a char in a String with a String It is used to convert all the
	 * quotation marks in backslash quote before asserting the content of a
	 * message in Jess.
	 * 
	 * @return the new String
	 */
	private String stringReplace(String str, char oldChar, String s) {
		int len = str.length();
		int i = 0;
		int j = 0;
		int k = 0;
		char[] val = new char[len];
		str.getChars(0, len, val, 0); // put chars into val

		char[] buf = new char[len * s.length()];

		while (i < len) {
			if (val[i] == oldChar) {
				s.getChars(0, s.length(), buf, j);
				j += s.length();
			} else {
				buf[j] = val[i];
				j++;
			}

			i++;
		}

		return new String(buf, 0, j);
	}

	/**
	 * makeasserts a fact representing an ACLMessage in Jess. It is called after
	 * the arrival of a message.
	 */
	private void makeassert(String fact) {
		try {
			jess.executeCommand(fact);
		} catch (JessException re) {
			re.printStackTrace(System.err);
		}
	}

	/**
	 * Remove the first and the last character of the string (if it is a
	 * quotation mark) and convert all backslash quote in quote It is used to
	 * convert a Jess content into an ACL message content.
	 */
	private String unquote(String str) {
		String t1 = str.trim();

		if (t1.startsWith("\"")) {
			t1 = t1.substring(1);
		}

		if (t1.endsWith("\"")) {
			t1 = t1.substring(0, t1.length() - 1);
		}

		int len = t1.length();
		int i = 0;
		int j = 0;
		int k = 0;
		char[] val = new char[len];
		t1.getChars(0, len, val, 0); // put chars into val

		char[] buf = new char[len];

		boolean maybe = false;

		while (i < len) {
			if (maybe) {
				if (val[i] == '\"') {
					j--;
				}

				buf[j] = val[i];
				maybe = false;
				i++;
				j++;
			} else {
				if (val[i] == '\\') {
					maybe = true;
				}

				buf[j] = val[i];
				i++;
				j++;
			}
		}

		return new String(buf, 0, j);
	}

	/**
	 * Insert the first and the last character of the string as a quotation mark
	 * Replace all the quote characters into backslash quote. It is used to
	 * convert an ACL message content into a Jess content.
	 */
	private String quote(java.lang.String str) {
		// replace all chars " in \ "
		return "\"" + stringReplace(str, '"', "\\\"") + "\"";
	}

	/**
	 * This method searches in the local cache for the full AID of the passed
	 * agentName. If not found it creates a new AID where only the guid is set.
	 **/
	public AID getAIDFromCache(String agentName) {
		AID result;
		result = (AID) AIDCache.get(agentName);

		if (result == null) {
			result = new AID(agentName);
		}

		return result;
	}

	/**
	 * This method searches in the local cache for the full AID of the passed
	 * list of agent names.
	 * 
	 * @param context
	 *            represents the Rete engine context needed to resolve the value
	 *            of JESS variables
	 * @param list
	 *            is a ValueVector of agent names
	 * @return a List of AID
	 */
	public List getAIDListFromCache(Context context, ValueVector list) {
		ArrayList l = new ArrayList();

		for (int i = 0; i < list.size(); i++) {
			try {
				l.add(getAIDFromCache(list.get(i).stringValue(context)));
			} catch (JessException je) {
			}
		}

		return l;
	}

	/**
	 * put a new AID in the local cache. If one exists already with the same
	 * agentName, it is overwritten
	 */
	public void putAIDInCache(AID aid) {
		AIDCache.put(aid.getName(), aid);
	}

	/** @return a String with the deftemplate command to be executed in Jess **/
	public String ACLJessTemplate() {
		return "(deftemplate ACLMessage (slot communicative-act) (slot sender) (multislot receiver) (slot reply-with) (slot in-reply-to) (slot envelope) (slot conversation-id) (slot protocol) (slot language) (slot ontology) (slot content) (slot encoding) (multislot reply-to) (slot reply-by))";
	}

	/**
	 * @return the ACLMessage representing the passed Jess Fact. This message
	 *         will be then sent by the caller.
	 */
	public ACLMessage JessFact2ACL(Context context, jess.ValueVector vv)
			throws jess.JessException {
		// System.err.println("JessFact2ACL "+vv.toString());
		int perf = ACLMessage.getInteger(vv.get(0).stringValue(context));
		ACLMessage msg = new ACLMessage(perf);
		System.out.println("******** Sender ********* " + vv.get(1).toString());

		if (vv.get(1).stringValue(context) != "nil") {
			msg.setSender(getAIDFromCache(vv.get(1).stringValue(context)));
		}

		if (vv.get(2).toString() != "nil") {
			List l = getAIDListFromCache(context, vv.get(2).listValue(context));

			for (int i = 0; i < l.size(); i++)
				msg.addReceiver((AID) l.get(i));
		}

		if (vv.get(3).stringValue(context) != "nil") {
			msg.setReplyWith(vv.get(3).stringValue(context));
		}

		if (vv.get(4).stringValue(context) != "nil") {
			msg.setInReplyTo(vv.get(4).stringValue(context));
		}

		// if (vv.get(5).stringValue(context) != "nil")
		// msg.setEnvelope(vv.get(5).stringValue(context));
		if (vv.get(6).stringValue(context) != "nil") {
			msg.setConversationId(vv.get(6).stringValue(context));
		}

		if (vv.get(7).stringValue(context) != "nil") {
			msg.setProtocol(vv.get(7).stringValue(context));
		}

		if (vv.get(8).stringValue(context) != "nil") {
			msg.setLanguage(vv.get(8).stringValue(context));
		}

		if (vv.get(9).stringValue(context) != "nil") {
			msg.setOntology(vv.get(9).stringValue(context));
		}

		if (vv.get(10).stringValue(context) != "nil") {
			// FIXME undo replace chars of JessBehaviour.java. Needs to be done
			// better
			msg.setContent(unquote(vv.get(10).stringValue(context)));
		}

		if (vv.get(11).stringValue(context) != "nil") {
			msg.setEncoding(vv.get(11).stringValue(context));
		}

		// System.err.println("JessFact2ACL type is "+vv.get(15).type());
		if (vv.get(12).toString() != "nil") {
			List l = getAIDListFromCache(context, vv.get(12).listValue(context));

			for (int i = 0; i < l.size(); i++)
				msg.addReplyTo((AID) l.get(i));
		}

		if (vv.get(13).stringValue(context) != "nil") {
			try {
				msg.setReplyByDate(new Date(Long.parseLong(vv.get(13)
						.stringValue(context))));
			} catch (Exception e) { /* do not care */
			}
		}

		return msg;
	}

	/**
	 * @return the String representing the facts (even more than one fact is
	 *         allowed, but this method just returns one fact) to be asserted in
	 *         Jess as a consequence of the receipt of the passed ACL Message.
	 *         The messate content is quoted before asserting the Jess Fact. It
	 *         is unquoted by the JessFact2ACL function.
	 */
	public String ACL2JessString(ACLMessage msg) {
		String fact;

		if (msg == null) {
			return "";
		}

		// I create a string that asserts the template fact
		fact = "(assert (ACLMessage (communicative-act "
				+ ACLMessage.getPerformative(msg.getPerformative());

		if (msg.getSender() != null) {
			fact = fact + ") (sender " + msg.getSender().getName();
			putAIDInCache(msg.getSender());
		}

		Iterator i = msg.getAllReceiver();

		if (i.hasNext()) {
			fact = fact + ") (receiver ";

			while (i.hasNext()) {
				AID aid = (AID) i.next();
				putAIDInCache(aid);
				fact = fact + aid.getName();
			}
		}

		if (!isEmpty(msg.getReplyWith())) {
			fact = fact + ") (reply-with " + msg.getReplyWith();
		}

		if (!isEmpty(msg.getInReplyTo())) {
			fact = fact + ") (in-reply-to " + msg.getInReplyTo();
		}

		// if (!isEmpty(msg.getEnvelope())) fact=fact+") (envelope " +
		// msg.getEnvelope();
		if (!isEmpty(msg.getConversationId())) {
			fact = fact + ") (conversation-id " + msg.getConversationId();
		}

		if (!isEmpty(msg.getProtocol())) {
			fact = fact + ") (protocol " + msg.getProtocol();
		}

		if (!isEmpty(msg.getLanguage())) {
			fact = fact + ") (language " + msg.getLanguage();
		}

		if (!isEmpty(msg.getOntology())) {
			fact = fact + ") (ontology " + msg.getOntology();
		}

		if (msg.getContent() != null) {
			fact = fact + ") (content " + quote(msg.getContent());
		}

		if (!isEmpty(msg.getEncoding())) {
			fact = fact + ") (encoding " + msg.getEncoding();
		}

		i = msg.getAllReplyTo();

		if (i.hasNext()) {
			fact = fact + ") (reply-to ";

			while (i.hasNext()) {
				AID aid = (AID) i.next();
				putAIDInCache(aid);
				fact = fact + aid.getName();
			}
		}

		if (msg.getReplyByDate() != null) {
			fact = fact + ") (reply-by " + msg.getReplyByDate().getTime();
		}

		fact = fact + ")))";

		return fact;
	}

	/**
	 * This class implements the Jess userfunction to send ACLMessages directly
	 * from Jess. It can be used by Jess by using the name <code>send</code>.
	 */
	public class JessSend implements Userfunction {
		// data
		Agent my_agent;
		RoverBehavior bjb;

		public JessSend(Agent a, RoverBehavior b) {
			my_agent = a;
			bjb = b;
		}

		// The name method returns the name by which the function appears in
		// Jess
		public String getName() {
			return ("send");
		}

		// Called when (send ...) is encountered
		public Value call(ValueVector vv, Context context) throws JessException {
			// for (int i=0; i<vv.size(); i++) {
			// System.out.println(" parameter " + i + "=" + vv.get(i).toString()
			// +
			// " type=" + vv.get(i).type());
			// }
			// ////////////////////////////////
			// Case where JESS calls (send ?m)
			if (vv.get(1).type() == RU.VARIABLE) {
				// Uncomment for JESS 5.0 vv =
				// context.getEngine().findFactByID(vv.get(1).factIDValue(context));
				vv = context.getEngine().findFactByID(
						vv.get(1).factValue(context).getFactId()); // JESS6.0
			}
			// ////////////////////////////////
			// Case where JESS calls (send (assert (ACLMessage ...)))
			else if (vv.get(1).type() == RU.FUNCALL) {
				Funcall fc = vv.get(1).funcallValue(context);
				vv = fc.get(1).factValue(context);
			}

			ACLMessage msg = bjb.JessFact2ACL(context, vv);
			my_agent.send(msg);

			return Funcall.TRUE;
		}
	} // end JessSend class
} // end JessBehaviour
