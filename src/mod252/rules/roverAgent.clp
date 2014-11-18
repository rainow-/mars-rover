(bind ?explores (new java.util.ArrayList))
(bind ?rand (new java.util.Random))
(bind ?from down)
(bind ?carrying false)

(deftemplate environment
	(slot signal (type FLOAT))
    (slot obstacle)
    (slot sample)
    (slot crumb)
    (slot spaceship)
    (slot direction)    
)

(deftemplate action
	(slot do)
    (slot at)
)

(deftemplate best_action
	(slot do)
    (slot at)
)

(defrule react
    ?this_env <- (environment (signal ?this_signal) (obstacle ?this_obstacle) 
        (sample ?this_sample) (crumb ?this_crumb) (spaceship ?this_spaceship) (direction ?this_direction))
    =>
	(if	(= ?carrying true) then
        (printout t "Rover is carrying sample" crlf)
        (if (= ?this_direction here) then
            (if (= ?this_spaceship true) then
        		(assert (best_action (do drop) (at ?this_direction)))
                else
                (assert (action (do nothing) (at here)))
            )
        else
            (printout t "Rover is not carrying sample" crlf)
    		(find_base ?this_env)
        )
    else
     	(if (<> ?this_direction here ) then
            (find_sample ?this_env)
        	else (if(= ?this_sample true) then
                (printout t "There is a sample here!!!!!!!!!!!!!!")
        		(assert (best_action (do gather) (at ?this_direction)))  
                else
                (assert (action (do nothing) (at here)))
            )
        )   	
    )
    
    (retract ?this_env)
)

(deffunction find_base(?this_env)
    (if(= ?this_env.spaceship true) then 
     		(assert (action (do drop) (at ?this_env.direction)))
        else (if (and (= ?this_env.crumb true) (<> (opposite_direction ?from))) then
        	(assert (action (do follow_crumbs) (at ?this_env.direction)))
        else (if (and (> ?this_env.signal ?signal-here) (= ?this_env.obstacle false)) then
                (assert (action (do follow_signal) (at ?this_env.direction)))
            else (assert (action (do turn_around) (at ?this_env.direction)))
            )
        )   
    )
)

(deffunction find_sample(?this_env)
	(if(= ?this_env.sample true) then
    	(assert (action (do gather) (at ?this_env.direction)))
    else (if (and (and (> ?this_env.signal 0) (= ?this_env.obstacle false) (= ?this_env.crumb false)))then
        (assert (action (do explore) (at ?this_env.direction))) 
  	else (if (and (= ?this_env.crumb true) (<> (opposite_direction ?from) ?this_env.direction)) then
        (assert (action (do follow_crumbs) (at ?this_env.direction)))
    else (assert (action (do turn_around) (at ?this_env.direction)))
            )       
        )
    )    
)

(deffunction opposite_direction(?this_dir)
	(if (= ?this_dir left) then
        return right
    else (if (= ?this_dir right) then 
        return left
    else (if (= ?this_dir down) then
        return up
    else (if (= ?this_dir up) then
        return down
    else
    	return ?from 
                )           
            )
        )    
    )    
)

;clean up
(defrule clean_up
    ?down <- (action (do ?do_down) (at down)) 
    ?right <- (action (do ?do_right) (at right)) 
    ?up <- (action (do ?do_up) (at up)) 
    ?left <- (action (do ?do_left) (at left)) 
    ?here <- (action (do ?) (at here))
    =>
    (if (= ?carrying true) then
     	(carry_priority ?left ?right ?down ?up)
        else 
        (not_carry_priority ?left ?right ?down ?up)
        (if (> (?explores size) 0) then (random_direction))
    )
    
    (retract ?left)
    (retract ?right)
    (retract ?down)
    (retract ?up)
)

(deffunction random_direction()
    (store dir (?explores get (?rand nextInt (?explores size))))
    (store act explore)
)

(defrule best
    ?best <- (best_action (do ?best_do) (at ?best_at))
    =>
    (if (= ?best_do turn_around) then (store dir (opposite_direction ?best_at))
        else
        (store dir ?best_at)
    )
    (store act ?best_do)
    (retract ?best)
)

(deffunction not_carry_priority(?left ?right ?down ?up)
    (foreach ?a (create$ gather explore follow_crumbs turn_around)
        (if (> (?explores size) 0) then (return))
		(foreach ?act (create$ ?left ?right ?down ?up)
    		(if (and (= ?act.do ?a) (<> ?a explore)) then
                (assert (best_action (do ?act.do) (at ?act.at)))
                (return)
            )
            else (if (and (= ?a explore) (= ?a ?act.do)) then
           		(?explores add ?act.at)	 
            )
        )    
	)   
)

(deffunction carry_priority(?left ?right ?down ?up)
    (foreach ?a (create$ drop follow_crumbs follow_signal turn_around)
		(foreach ?act (create$ ?left ?right ?down ?up)
            (if(= ?act.do ?a) then
                (assert (best_action (do ?act.do) (at ?act.at)))
                (return)
            )
        )    
	)   
)