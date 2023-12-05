package pp202302.project.impl

// Problem 4: Fill it!
val calculator: String =
  """
  (let
    (def isDigit (char)
      (if (= char "0") 1
        (if (= char "1") 1
          (if (= char "2") 1
            (if (= char "3") 1
              (if (= char "4") 1
                (if (= char "5") 1
                  (if (= char "6") 1
                    (if (= char "7") 1
                      (if (= char "8") 1
                        (if (= char "9") 1
                          0)))))))))))
    (def isOper (char)
      (if (= char "+") 1
        (if (= char "-") 1
          (if (= char "*") 1
            (if (= char "/") 1
              0)))))
    (def parseDigit (char)
      (if (= char "0") 0
        (if (= char "1") 1
          (if (= char "2") 2
            (if (= char "3") 3
              (if (= char "4") 4
                (if (= char "5") 5
                  (if (= char "6") 6
                    (if (= char "7") 7
                      (if (= char "8") 8
                        (if (= char "9") 9
                          nil)))))))))))
    (def parseInt (str)
      (if (len str)
        (let
          (val slen (len str))
          (val head (substr str 0 (- slen 1)))
          (val last (substr str (- slen 1) slen))
          (+ (* 10 (app parseInt head)) (app parseDigit last)))
        0))

    (def tokenize (acc str)
      (if (len str)
        (let
          (val slen (len str))
          (val fchar (substr str 0 1))
          (val rest (substr str 1 slen))
          (if (app isDigit fchar)
            (app tokenize (+ acc fchar) rest)
            (if (app isOper fchar)
              (if (len acc)
                (cons (app parseInt acc) (cons fchar (app tokenize "" rest)))
                (cons fchar (app tokenize "" rest)))
              (cons "ERROR" nil))))
        (if (len acc)
          (cons (app parseInt acc) nil)
          nil)))

    (def validate (tokens)
      (let
        (def AND (a (by-name b)) (if a b 0))
        (def NOT (a) (if a 0 1))
        (def vEven (tokens)
          (if (nil? tokens)
            1
            (app AND
              (string? (fst tokens))
              (app AND
                (app isOper (fst tokens))
                (app validate (snd tokens))))))
        (app AND
          (app NOT (nil? tokens))
          (app AND
            (int? (fst tokens))
            (app vEven (snd tokens))))))

    (def evalterm (acc tokens)
      (if (nil? tokens)
        (cons acc nil)
        (let
          (val oper (fst tokens))
          (val next (fst (snd tokens)))
          (val rest (snd (snd tokens)))
          (if (= oper "*")
            (app evalterm (* acc next) rest)
            (if (= oper "/")
              (app evalterm (/ acc next) rest)
              (cons acc tokens))))))

    (def eval (acc tokens)
      (if (nil? tokens)
        acc
        (let
          (val oper (fst tokens))
          (val back (snd tokens))
          (val next (app evalterm (fst back) (snd back)))
          (if (= oper "+")
            (app eval (+ acc (fst next)) (snd next))
            (app eval (- acc (fst next)) (snd next))))))

    (def evalline (line)
      (let
        (val tokens (app tokenize "" line))
        (if (app validate tokens)
          (let
            (val term (app evalterm (fst tokens) (snd tokens)))
            (app eval (fst term) (snd term)))
          "parse error")))

    (defIO repl ()
      (readline line)
      (runIO next
        (let
          (defIO replline ()
            (print (app evalline line))
            (print "
")
            (app repl))
          (if (= line "exit")
            nil
            (app replline))))
      (runIO _ next)
      nil)

    (app repl)
  )
  """
