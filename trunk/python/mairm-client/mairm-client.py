import e32
import btsocket, appuifw, key_codes

from sensors import AbstractSensor, init_sensor

def dummy():
  pass

class Mode:
  MOUSE = 0
  SCROLLING = 1
  GESTURE = 2

class Application:
  def __init__(self):
    self.sensor = init_sensor()
    
    self.bt_connect()
    self.mode = Mode.MOUSE
    
    self.sensor.set_callback(self.sensor_event)
    
  def bt_povezi(self):
    self.sock = btsocket.socket(btsocket.AF_BT,btsocket.SOCK_STREAM)
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

  def sensor_event(self, x, y, z):
    text = '{"mouse":{"x": %f, "y": %f, "z": %f' % (x, y, z)
    if self.mode == Mode.SCROLLING:
      text += ', "middlebutton": "scrolling"'
    
    test += '}}'
    self.sock.send(text)
  
  def close(self):
    self.sensor.cleanup()

  def keypress(self, event):
    if event["scancode"] == key_codes.EScancodeLeftSoftkey:
      text = '{"mouse":{'
      text += '"leftbutton":'
      if event["type"] == appuifw.EEventKeyDown:
        text += "down"
      elif event["type"] == appuifw.EEventKeyUp:
        text += "up"
    elif event["scancode"] == key_codes.EScancodeRightSoftkey:
      text = '{"mouse":{'
      text += '"rightbutton":'
      if event["type"] == appuifw.EEventKeyDown:
        text += "down"
      elif event["type"] == appuifw.EEventKeyUp:
        text += "up"
    elif event["scancode"] == key_codes.EScancodeYes:
      if event["type"] == appuifw.EEventKeyUp:
        if self.mode == Mode.MOUSE:
          self.mode = Mode.SCROLLING
        else:
          self.mode = Mode.MOUSE
      return
    else:
      return
  
    text += "}}"
  
    self.sock.send(text)

# Init sensors

app = Application()

appuifw.title = 'MairM Client'
canvas = appuifw.Canvas(event_callback = app.keypress)
appuifw.app.exit_key_handler = dummy
appuifw.app.body = canvas
e32.ao_sleep(15000)
app.close()
