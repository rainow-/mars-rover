(bind ?explores (new java.util.ArrayList))
(bind ?rand (new java.util.Random))
(bind ?came-from here)

(deftemplate environment
	(slot signal (type FLOAT))
    (slot obstacle)
    (slot sample)
    (slot crumb)
    (slot spaceship)
    (slot direction)    
)

(deftemplate rover
    (slot action)
    (slot signal (type FLOAT))
	(slot carrying-sample)
)

(deftemplate action
	(slot do)
    (slot at)
)

(deftemplate best_action
	(slot do)
    (slot at)
)


(defrule reaction
    ?this_env <- (environment (signal ?this_signal) (obstacle ?this_obstacle) 
        (sample ?this_sample) (crumb ?this_crumb) (spaceship ?this_spaceship) (direction ?this_direction))
    =>
	(if	(= ?rover.carrying-sample true) then
    	(find_base ?this_env)
    else
     	(if (and (= ?this_direction here) (= ?this_sample true) ) then
            (assert (best_action (do gather) (at ?this_direction)))
        	else
        	(find_sample ?this_env)  
        )   	
    )
    
    (retract ?this_env)
)

(deffunction find_base(?this_env)
    (if(= ?this_env.spaceship true) then 
     		(assert (action (do drop) (at ?this_env.direction)))
        else (if (= ?this_env.crumb true) then
        	(assert (action (do follow_crumbs) (at ?this_env.direction)))
        else (if (and (> ?this_env.signal ?rover.signal) (= ?this_env.obstacle false)) then
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
  	else (if (and (= ?this_env.crumb true) (<> ?came-from ?this_env.direction)) then
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
    else
        return down            
            )
        )    
    )    
)

;clean up
(defrule clean_up
    ?current <- (action (do ?do_current) (at here)) 
    ?down <- (action (do ?do_down) (at down)) 
    ?right <- (action (do ?do_right) (at right)) 
    ?up <- (action (do ?do_up) (at up)) 
    ?left <- (action (do ?do_left) (at left)) 
    =>
    
    (if (= ?rover.carrying-sample true) then
     	(carry_priority ?left ?right ?down ?up ?current)
        else 
        (not_carry_priority ?left ?right ?down ?up ?current)
        (if (> (?explores size) 0) then (random_direction))
    )
    
    (retract ?left)
    (retract ?right)
    (retract ?down)
    (retract ?up)
    (retract ?current)
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

(deffunction not_carry_priority(?left ?right ?down ?up ?current)
    (foreach ?a (create$ gather explore follow_crumbs turn_around)
        (if (> (?explores size) 0) then return)
		(foreach ?act (create$ ?left ?right ?down ?up ?current)
    		(if (and (= ?act.do ?a) (<> ?a explore)) then
                (assert (best_action (do ?act.do) (at ?act.at)))
                (return)
             else (if (and (= ?act.do ?a ) (<> ?act.at here)) then
                    (?explores add ?act.at)
                )
            )
        )    
	)   
)

(deffunction carry_priority(?left ?right ?down ?up ?current)
    (foreach ?a (create$ drop follow_crumbs follow_signal turn_around)
		(foreach ?act (create$ ?left ?right ?down ?up ?current)
            (if(= ?act.do ?a) then
                (assert (best_action (do ?act.do) (at ?act.at)))
                (return)
            )
        )    
	)   
)