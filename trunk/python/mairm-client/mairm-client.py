import e32
import btsocket, appuifw, key_codes

from sensors import *

def bt_povezi():
  global sock
  sock = btsocket.socket(btsocket.AF_BT,btsocket.SOCK_STREAM)
  target = ''
  (address, services) = btsocket.bt_discover()
  if len(services) > 1:
    choices = services.keys()
    choices.sort()
    choice = appuifw.popup_menu([unicode(services[x]) + ": " + x for x in choices], u'Choose port:')
    target = (address, services[choices[choice]])
  else:
    target = (address,services.values()[0])
  
  print "Povezujem..." + str(target)
  sock.connect(target)
  print "OK."


def sensor_event(x, y, z):
  global sock
  text = '{"mouse":{"x": %f, "y": %f, "z": %f}}' % (x, y, z)
  sock.send(text)

def keypress(event):
  text = '{"mouse":{'
  
  if event["scancode"] == key_codes.EScancodeLeftSoftkey:
    text += '"leftbutton":'
    if event["type"] == appuifw.EEventKeyDown:
      text += "down"
    elif event["type"] == appuifw.EEventKeyUp:
      text += "up"
  
  text += "}}"
  
  sock.send(text)

# Init sensors
try:
  sensor = OldSensor()
except:
  raise Exception('No supported accelometer sensors API.')

bt_povezi()

sensor.set_callback(sensor_event)

canvas = appuifw.Canvas(event_callback = keypress)
appuifw.app.body = canvas
e32.ao_sleep(15000)
acc.disconnect()