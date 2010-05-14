# -*- coding: utf-8 -*-

def parse_string(text):
  output = u''
  for char in text:
    modifiers = u''
    if char == u'@':
      char = u'AT'
    elif char == u'"':
      char = u'DOUBLE QUOTE'
    elif char == u"'":
      char = u'QUOTE'
    elif char == u",":
      char = u'COMMA'
    elif char == u'.':
      char = u'NUMPAD .'
    elif char.isupper():
      char = char.lower()
      modifiers += u'SHIFT'
    
    output += u'{"keyboard":{"keys":"%s", "modifiers":"%s"}}\n' % (char, modifiers)
  
  return output
