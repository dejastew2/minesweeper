(deftemplate possible-state (multislot mines))
(deftemplate to-check (slot id))
(deftemplate known-mines (multislot mines))

; <<<  DEFGLOBALS  >>>
; EMPTY_SQUARE (=0)
; FLAGGED_MINE (=-2)
; UNKNOWN      (=-4)
; <<<  DEFGLOBALS  >>>

; <<<  DEFTEMPLATES  >>>
;   (MineSquare
;      (slot id) - integer
;      (slot row) - integer
;      (slot col) - integer
;      (slot status ?status) - integer
;      (multislot surroundingUnknowns) - integer list
;      (slot numSurroundingFlags) - integer
;   )
;
;   (MinefieldVariables
;      (slot numberMines) - integer
;      (slot mineHeight) - integer
;      (slot mineWidth) - integer
;   )
;
; <<<  DEFTEMPLATES  >>>

; Hooks to GUI:
;    (reveal-square ?id)
;    (flag-square ?id)

; pass in a multifield of MineSquares (id's) and
;  this function will mark each MineSquare as a mine
(deffunction flagAllSurroundingUnknowns ($?surrUnknowns)
	(foreach ?s $?surrUnknowns (flag-square ?s))
)

; pass in a multifield of MineSquares (id's) and
;  this function will reveal each MineSquare;
(deffunction revealAllSurroundingUnknowns ($?surrUnknowns)
	(foreach ?s $?surrUnknowns (reveal-square ?s))
)

; This rule runs at the beginning and prints the field info
(defrule display-field-info
	(MinefieldVariables
		(numberMines ?numberMines)
		(mineHeight ?mineHeight)
		(mineWidth ?mineWidth))
	=>
    (printout t crlf "======= NEW GAME =======" crlf
	 			"This field has " ?numberMines " mines, and is "
                ?mineHeight " blocks high and "
                ?mineWidth " blocks wide." crlf crlf))

; This rule fires when the number of surrounding flags plus the 
; number of unknowns equals the tile status
(defrule mark-by-exhaustion
	(MineSquare
		(row ?r)
		(col ?c)
		(status ?s&:(> ?s 0))
		(numSurroundingFlags ?n)
		(surroundingUnknowns $?u&:(eq (+ (length$ $?u) ?n) ?s)))
	=>
	(printout t " --> MARKING by exhaustion: (r=" ?r ", c=" ?c ")" crlf)
	(flagAllSurroundingUnknowns $?u))

; This rule fires when the status of a tile is equal to the number of 
; surrounding flags (all mines are accounted for)
(defrule reveal-by-exhaustion
	(MineSquare
		(row ?r)
		(col ?c)
		(status ?s&:(> ?s 0))
		(numSurroundingFlags ?s)
		(surroundingUnknowns $?u&:(> (length$ $?u) 0)))
	=>
	(printout t " --> REVEALING by exhaustion: (r=" ?r ", c=" ?c ")" crlf)
	(revealAllSurroundingUnknowns $?u))
	
(deffunction assert-all-possible-states (?remainingMines $?surrUnknowns)
	(bind ?maxbin 1)
	(foreach ?i $?surrUnknowns (bind ?maxbin (* ?maxbin 2)))
	(bind ?i 0)
	(while (< ?i ?maxbin) 
		(bind $?surTemp $?surrUnknowns)
		(bind $?sur (explode$ ""))
		(bind ?iTemp ?i)
		(bind ?j (- (length$ $?surrUnknowns) 1))
		(while (> ?j -1)
			(bind ?powOfTwo 1)
			(bind ?k 0)
			(while (< ?k ?j)
				(bind ?powOfTwo (* ?powOfTwo 2))
				(bind ?k (+ ?k 1)))
			(if (< ?powOfTwo (+ ?iTemp 1)) then
				(bind ?iTemp (- ?iTemp ?powOfTwo))
				(bind $?sur (union$ $?sur (subseq$ $?surTemp 1 1))))
			(bind $?surTemp (rest$ $?surTemp))
			(if (eq (length$ $?surTemp) 0) then
				(bind ?j -1)
			else
				(bind ?j (- ?j 1))))
		(if (eq (length$ $?sur) ?remainingMines) then 
			(assert (possible-state (mines $?sur))))
		(bind ?i (+ ?i 1)))
)

(deffunction get-surr-knowns (?id ?row ?col ?width ?height $?surrUnknowns) 
	(bind $?top (create$ (- (- ?id ?width) 1) (- ?id ?width) (+ (- ?id ?width) 1)))
	(bind $?bottom (create$ (- (+ ?id ?width) 1) (+ ?id ?width) (+ (+ ?id ?width) 1)))
	(bind $?left (create$ (- (- ?id ?width) 1) (- ?id 1) (- (+ ?id ?width) 1)))
	(bind $?right (create$ (+ (- ?id ?width) 1) (+ ?id 1) (+ (+ ?id ?width) 1)))
	(bind $?sur (union$ $?top $?bottom $?left $?right))
	(if (eq (+ ?row 1) ?height) then (bind $?sur (complement$ $?bottom $?sur)))
	(if (eq ?row 0) then (bind $?sur (complement$ $?top $?sur)))
	(if (eq (+ ?col 1) ?width) then (bind $?sur (complement$ $?right $?sur)))
	(if (eq ?col 0) then (bind $?sur (complement$ $?left $?sur)))
	(bind $?sur (complement$ $?surrUnknowns $?sur))
	;(printout t $?sur crlf)
	(return $?sur)
)
	
(defrule extract-1
	(declare (salience -2))
	(MinefieldVariables
		(mineHeight ?mineHeight)
		(mineWidth ?mineWidth))
	(MineSquare 
		(id ?i)
		(row ?r)
		(col ?c)
		(status ?s&:(> ?s 1))
		(numSurroundingFlags ?n&:(> (- ?s ?n) 1))
		(surroundingUnknowns $?u&:(> (length$ $?u) 0)))
	;(not (possible-state (mines $?)))
	=>
	(bind $?knowns (get-surr-knowns ?i ?r ?c ?mineWidth ?mineHeight $?u))
	(if (> (length$ $?knowns) 0) then
		(printout t " --> EXTRACTING from incomplete data: (r=" ?r ", c=" ?c ")" crlf)
		(foreach ?checkID $?knowns (assert (to-check (id ?checkID))))
		(assert-all-possible-states (- ?s ?n) $?u)))

(defrule extract-2
	?todel <- (to-check (id ?i))
	(MineSquare 
		(id ?i)
		(status 0))
	=>
	(retract ?todel))

(defrule extract-22
	?todel <- (to-check (id ?i))
	(MineSquare 
		(id ?i)
		(surroundingUnknowns $?u&:(eq (length$ $?u) 0)))
	=>
	(retract ?todel))
	
(defrule extract-3
	(to-check (id ?i))
	(MineSquare 
		(id ?i)
		(status ?s&:(> ?s 0))
		(numSurroundingFlags ?n)
		(surroundingUnknowns $?u&:(> (length$ $?u) 0)))
	?tocheck <- (possible-state (mines $?m))
	=>
	(if (> (length$ (intersection$ $?m $?u)) (- ?s ?n)) then 
		(retract ?tocheck)))

(defrule extract-33
	(declare (salience -1))
	?todel <- (to-check (id ?i))
	(MineSquare 
		(id ?i)
		(surroundingUnknowns $?u&:(> (length$ $?u) 0)))
	;(not (possible-state (mines $?m&:(> (length$ (intersection$ $?m $?u)) 0))))
	=>
	(retract ?todel))

(defrule extract-4
	(not (to-check (id ?i)))
	(not (known-mines (mines $?k)))
	?todel <- (possible-state (mines $?m))
	=>
	(retract ?todel)
	(assert (known-mines (mines $?m))))
	
(defrule extract-5
	(not (to-check (id ?i)))
	?todel <- (known-mines (mines $?k))
	?todel2 <- (possible-state (mines $?m))
	=>
	(retract ?todel)
	(retract ?todel2)
	(assert (known-mines (mines (intersection$ $?m $?k)))))
	
(defrule extract-6
	(not (possible-state (mines $?m)))
	?todel <- (known-mines (mines $?k))
	=>
	(foreach ?n $?k (flag-square ?n))
	(retract ?todel))
