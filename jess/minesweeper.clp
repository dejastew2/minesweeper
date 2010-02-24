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
)

; pass in a multifield of MineSquares (id's) and
;  this function will reveal each MineSquare;
(deffunction revealAllSurroundingUnknowns ($?surrUnknowns)
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

; here is a test rule.
; find a minesquare that's a 1 and reveal the square to the right of it.
; this also prints the minefieldvariables deftemplate values
;(defrule test
;   (MineSquare
;      (id ?id1)
;      (row ?row1)
;      (col ?col1)
;      (status 1)
;      (surroundingUnknowns $?surrUn1)
;      (numSurroundingFlags $?numSurrFlags)
;   )
;   (MinefieldVariables
;      (mineSquaresKnown ?mineSquaresKnown)
;      (numberMines ?numberMines)
;      (mineHeight ?mineHeight)
;      (mineWidth ?mineWidth)
;   )
;
;=>
;    (printout t "?numberMines: " ?numberMines
;                ", ?mineHeight: " ?mineHeight
;                ", ?mineWidth: " ?mineWidth crlf)
;    (reveal-square (+ 1 ?id1))
;)
