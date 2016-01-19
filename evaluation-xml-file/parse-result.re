# Remove words
(Pr[e|é]cision[^:]*à [^:]*:) -> nothing
Moyenne[^:,\w]* -> nothing
Score[^:]*:  -> nothing

# Change number delimited by . to number delimited by ,
([0-1]).([0-9]+) -> \1,\2

# Extract 5 (first element)
,"[^\w]*([0-9.]+)[^"]* -> ,"\1

# Extract 10 (second element)
,"[^\w]*([0-9.]+)[^0-9]*([0-9.]+)[^"]* -> ,"\1

# Extract 25 (third element)
,"[^\w]*[0-9,]*[^0-9,]+[0-9,]+[^0-9,]+([0-9,]+)[^"]* -> ,"\1
