package mod252.utils;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class DFUtils {
	
	public final static String SERVICE_NAME = "";
	public final static String SERVICE_TYPE = "";
	
	public static DFAgentDescription getDF(AID aid, String serviceName, String serviceType){
		ServiceDescription sd = new ServiceDescription();
		sd.setName(serviceName);
		sd.setType(serviceType);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(aid);
		dfd.addServices(sd);
	    return dfd;
	}
}
