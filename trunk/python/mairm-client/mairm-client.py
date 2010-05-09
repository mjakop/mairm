# -*- coding: utf-8 -*-
import e32
import btsocket, appuifw, key_codes

#from sensors import AbstractSensor, init_sensor

def dummy():
  pass

def test():
  audio.say(u'Wiiii')

class Mode:
  MOUSE = u'Mouse'
  SCROLLING = u'Scrolling'
  GESTURE = u'Gesture'
  SGESTURE = u'Suspended gesture'

class Application:
  def __init__(self):
    self.locker = e32.Ao_lock()
    self.sleeper = e32.Ao_timer()
    self.sensor = init_sensor()
    self.buffer = ''
    self.suspend_buffer = ''
    
    self.mode = Mode.MOUSE
    self.sock = None
    
    self.sensor.set_callback(self.sensor_event)
    
    self.exit = False
  
  def loop(self):
    # Initialize gui
    self.init_gui()
    while not self.exit:
      self.redraw_event()
      self.locker.wait()
    
  def bt_connect(self):
    try:
      target = ''
      (address, services) = btsocket.bt_discover()
      choices = services.keys()
      choices.sort()
      choice = appuifw.popup_menu([unicode(services[x]) + ": " + x for x in choices], u'Choose port:')
      target = (address, services[choices[choice]])
  
      self.sock = btsocket.socket(btsocket.AF_BT, btsocket.SOCK_STREAM)
      self.sock.connect(target)
      return True
    except:
      if self.sock:
        del self.sock
      appuifw.note(u'Failed to connect', 'error')
      return False

  def sensor_event(self, x, y, z):
    if self.mode == Mode.SGESTURE or self.mode == Mode.GESTURE:
      text = '{"gesture":{'
      if self.mode == Mode.SGESTURE and self.suspend_buffer == '':
        text += '"start":"true,"'
    else:
      text = '{"mouse":{'
    
    text += '"x": %f, "y": %f, "z": %f' % (x, y, z)
    if self.mode == Mode.SCROLLING:
      text += ', "middlebutton": "scrolling"'
    text += '}}\n'
    
    if self.mode == Mode.SGESTURE:
      self.suspend_buffer += text
    
    try:
      self.sock.send(self.buffer + text)
      self.buffer = ''
    except:
      pass
  
  def close(self):
    self.sensor.cleanup()
    if self.sock:
      self.sock.close()
    self.exit = True
    appuifw.body = None
    self.locker.signal()
  
  def init_gui(self):
    if not appuifw.touch_enabled():
      self.buttons = None
      return
    
    size = self.canvas.size
    top = 40
    bottom = size[1]
    space = 20
    width = (size[0] - 2 * space) / 3
    
    self.buttons = ((0, top), (width, bottom),
                    (width + space, top), (width * 2 + space, bottom),
                    (width * 2 + space * 2, top), (width * 3 + space * 2, bottom))
  
  def simulate_softkey(self, event):
    if self.buttons is None:
      return (None, None)
    
    if event['type'] == key_codes.EButton1Down:
      type = appuifw.EEventKeyDown
    elif event['type'] == key_codes.EButton1Up:
      type = appuifw.EEventKeyUp
    else:
      return (None, None)
    
    if (event['pos'] >= self.buttons[0]) and (event['pos'] <= self.buttons[1]):
      return (key_codes.EScancodeLeftSoftkey, type)
    elif (event['pos'] >= self.buttons[2]) and (event['pos'] <= self.buttons[3]):
      return (key_codes.EScancodeSelect, type)
    elif (event['pos'] >= self.buttons[4]) and (event['pos'] <= self.buttons[5]):
      return (key_codes.EScancodeRightSoftkey, type)
    else:
      return (None, None)
  
  def redraw_event(self):
    self.canvas.clear()
    
    if self.buttons is not None:
      self.canvas.rectangle(self.buttons, fill=(200, 200, 200))
    
    self.canvas.text((0, 12), u'Mode: %s' % self.mode)

  def resume(self):
    self.mode = Mode.GESTURE
    self.buffer += self.suspend_buffer
    self.suspend_buffer = ''
    self.locker.signal()

  def keypress(self, event):
    if 'pos' in event.keys():
      translated = self.simulate_softkey(event)
      event['scancode'] = translated[0]
      event['type'] = translated[1]

    if event['scancode'] == key_codes.EScancodeLeftSoftkey:
      text = '{"mouse":{"leftbutton":'
      if event['type'] == appuifw.EEventKeyDown:
        text += '"down"'
      elif event['type'] == appuifw.EEventKeyUp:
        text += '"up"'
    elif event['scancode'] == key_codes.EScancodeRightSoftkey:
      text = '{"mouse":{"rightbutton":'
      if event['type'] == appuifw.EEventKeyDown:
        text += '"down"'
      elif event["type"] == appuifw.EEventKeyUp:
        text += '"up"'
    elif event['scancode'] == key_codes.EScancodeSelect:
      if self.mode != Mode.SCROLLING and event['type'] == appuifw.EEventKeyDown:
        self.mode = Mode.SGESTURE
        self.sleeper.after(0.1, self.resume)
      elif event['type'] == appuifw.EEventKeyUp:
        if self.mode == Mode.SGESTURE:
          self.sleeper.cancel()
          self.mode = Mode.SCROLLING
        else:
          if self.mode == Mode.GESTURE:
            self.buffer += '{"gesture":{"end":"true"}}\n'
          self.mode = Mode.MOUSE
      self.locker.signal()
      return
    elif event['scancode'] == key_codes.EScancodeNo:
      appuifw.app.set_exit()
      return
    else:
      return
    
    text += "}}\n"
    
    try:
      self.buffer += text
      self.sock.send(self.buffer)
      self.buffer = ''
    except:
      pass
    
    self.locker.signal()

# Init sensors
app = Application()

appuifw.title = 'MairM Client'
appuifw.app.orientation = 'portrait'
appuifw.app.screen = 'full'
appuifw.app.directional_pad = False
app.canvas = appuifw.Canvas(event_callback = app.keypress, redraw_callback = lambda rect:app.redraw_event)
appuifw.app.exit_key_handler = dummy
appuifw.app.body = app.canvas
if app.bt_connect():
  app.loop()
app.close()
