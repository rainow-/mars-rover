(defrule proposal
 "When a 'cfp' message arrives from an agent ?s, this rule asserts a 
  'propose' message to the same sender and retract the just arrived message"
 ?m <- (ACLMessage (communicative-act CFP) (sender ?s) (content ?c) (receiver ?r))
 =>
; (send (assert (ACLMessage (communicative-act PROPOSE) (receiver ?s) (content ?c) )))
 (assert (ACLMessage (communicative-act PROPOSE) (sender ?r) (receiver ?s) (content ?c) ))
 (retract ?m)
)

(defrule send-a-message
 "When a message is asserted whose sender is this agent, the message is
  sent and then retracted from the knowledge base."
 (MyAgent (name ?n))
 ?m <- (ACLMessage (sender ?n))
 =>
 (send ?m)
 (retract ?m)
)

(watch facts)
(watch all)
(reset) 

(run)  
; if you put run here, Jess is run before waiting for a message arrival,
; if you do not put (run here, the agent waits before for the arrival of the 
; first message and then runs Jess.

(deftemplate environment
	(slot signal)
    (slot obstacle)
    (slot sample)
    (slot amount)    
)

(deftemplate rover
	(slot carrying-sample)
    (slot remaining-samples)
    (slot at-home)
)

(deftemplate action
	(slot do)
)

(defrule react
    ?action <- (environment (signal ?this_signal) (obstacle ?this_obstacle) (sample ?this_sample) (amount ?this_amount)) 
    => 
    (if(= ?this.obstacle true) 
        then (assert (action (do change_dir)))
  	else (if (and (= ?rover.carrying-sample true) (= ?rover.at-home true))
            then (assert (action (do drop_off))
                  )
    else (if (and (= ?rover.carrying-sample true) (= ?rover.at-home false))
        then (assert (action (do return_home)))
    else (if (= ?this.sample true)
        then (assert (action (do pick_up)))
    else (assert (action (do move))))
            )
       	)
    )
)






