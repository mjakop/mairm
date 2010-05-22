# -*- coding: utf-8 -*-
import e32
import btsocket, appuifw, key_codes

#from sensors import AbstractSensor, init_sensor
#from parsers import parse_string

# -*- coding: utf-8 -*-
import sensor

class AbstractSensor:
  def coord(self):
    return (0, 0, 0)
  
  def coord_cb(self):
    pass
  
  def set_callback(self, cb):
    pass
  
  def cleanup(self):
    pass

class OldSensor(AbstractSensor):
  def __init__(self):
    sensors = sensor.sensors()
    self.sensor = sensor.Sensor(sensors["AccSensor"]["id"], sensors["AccSensor"]["category"])
    self.cb = None
    
    self.sensor.connect(self.coord_cb)
  
  def coord_cb(self, data):
    if self.cb is None:
      return

    self.cb(data["data_1"], data["data_2"], data["data_3"])
  
  def set_callback(self, cb):
    self.cb = cb
  
  def cleanup(self):
    self.sensor.disconnect()

class S605thSensor(AbstractSensor):
  def __init__(self):
    self.sensor = sensor.AccelerometerXYZAxisData()
    self.sensor.set_measure_range(0)
    self.cb = None
  
    self.sensor.set_callback(data_callback = self.coord_cb)
    self.sensor.start_listening()
    self.count = 0
  
  def coord_cb(self):
    if self.cb is None:
      return
    
    # Need to switch x and y coordinates for 5800
    self.cb(self.sensor.y * 2, self.sensor.x * 2, self.sensor.z * 2)
  
  def set_callback(self, cb):
    self.cb = cb
  
  def cleanup(self):
    self.sensor.stop_listening()

def init_sensor():
  try:
    return OldSensor()
  except:
    try:
      return S605thSensor()
    except:
      raise Exception('No supported accelometer sensors API.')


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
      char = u'NUMPAD .'
    elif char == u".":
      char = u'NUMPAD ,'
    elif char.isupper():
      char = char.lower()
      modifiers += u'SHIFT'
    
    output += u'{"keyboard":{"keys":"%s", "modifiers":"%s"}}\n' % (char, modifiers)
  
  return output

def dummy():
  pass

def test():
  audio.say(u'Wiiii')

class Mode:
  MOUSE = u'Mouse'
  SCROLLING = u'Scrolling'
  GESTURE = u'Gesture'
  SGESTURE = u'Suspended gesture'
  KEYBOARD = u'Keyboard'

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
    if self.mode == Mode.KEYBOARD:
      return
    
    if self.mode == Mode.SGESTURE or self.mode == Mode.GESTURE:
      text = '{"gesture":{'
      if self.mode == Mode.SGESTURE and self.suspend_buffer == '':
        text += '"start":"true",'
    else:
      text = '{"mouse":{'
    
    text += '"x": %f, "y": %f, "z": %f' % (x, y, z)
    if self.mode == Mode.SCROLLING:
      text += ', "middlebutton": "scrolling"'
    text += '}}\n'
    
    if self.mode == Mode.SGESTURE:
      self.suspend_buffer += text
      return
    
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
    bottom = size[1] - 60
    space = 20
    width = (size[0] - 2 * space) / 3
    
    self.buttons = ((0, top), (width, bottom), # Left button
                    (width + space, top), (width * 2 + space, bottom), # Middle button
                    (width * 2 + space * 2, top), (width * 3 + space * 2, bottom), # Right button
                    (0, space + bottom), (width, size[1]), # Edit button
                    (width + space, space + bottom), (width * 2 + space, size[1]), # Remove (C)
                    (width * 2 + space * 2, space + bottom), (width * 3 + space * 2, size[1])) # New line (0)
  
  def inside(self, pos, button):
    return (pos[0] >= self.buttons[button * 2][0] and pos[1] >= self.buttons[button * 2][1]) and (pos[0] <= self.buttons[button * 2 + 1][0] and pos[1] <= self.buttons[button * 2 + 1][1])
  
  def simulate_softkey(self, event):
    if self.buttons is None:
      return (None, None)
    
    if event['type'] == key_codes.EButton1Down:
      type = appuifw.EEventKeyDown
    elif event['type'] == key_codes.EButton1Up:
      type = appuifw.EEventKeyUp
    else:
      return (None, None)
    
    if self.inside(event['pos'], 0):
      return (key_codes.EScancodeLeftSoftkey, type)
    elif self.inside(event['pos'], 1):
      return (key_codes.EScancodeSelect, type)
    elif self.inside(event['pos'], 2):
      return (key_codes.EScancodeRightSoftkey, type)
    elif self.inside(event['pos'], 3):
      return (key_codes.EScancodeEdit, type)
    elif self.inside(event['pos'], 4):
      return (key_codes.EScancodeBackspace, type)
    elif self.inside(event['pos'], 5):
      return (key_codes.EScancode0, type)
    else:
      return (None, None)
  
  def redraw_event(self):
    self.canvas.clear()
    
    if self.buttons is not None:
      self.canvas.rectangle(self.buttons, fill=(200, 200, 200))
    
      # Drawing texts
      # Left
      width = self.buttons[1][0] / 2
      self.canvas.text((self.buttons[0][0] + width, self.buttons[0][1] + 30), u'L')
      self.canvas.text((self.buttons[0][0] + width, self.buttons[0][1] + 50), u'e')
      self.canvas.text((self.buttons[0][0] + width, self.buttons[0][1] + 70), u'f')
      self.canvas.text((self.buttons[0][0] + width, self.buttons[0][1] + 90), u't')
      # Mode
      self.canvas.text((self.buttons[2][0] + width, self.buttons[2][1] + 30), u'M')
      self.canvas.text((self.buttons[2][0] + width, self.buttons[2][1] + 50), u'o')
      self.canvas.text((self.buttons[2][0] + width, self.buttons[2][1] + 70), u'd')
      self.canvas.text((self.buttons[2][0] + width, self.buttons[2][1] + 90), u'e')
      # Right
      self.canvas.text((self.buttons[4][0] + width, self.buttons[4][1] + 30), u'R')
      self.canvas.text((self.buttons[4][0] + width, self.buttons[4][1] + 50), u'i')
      self.canvas.text((self.buttons[4][0] + width, self.buttons[4][1] + 70), u'g')
      self.canvas.text((self.buttons[4][0] + width, self.buttons[4][1] + 90), u'h')
      self.canvas.text((self.buttons[4][0] + width, self.buttons[4][1] + 110), u't')
      
      # Text
      self.canvas.text((self.buttons[6][0] + 10, self.buttons[6][1] + 30), u'Keys')
      # Backspace (C)
      self.canvas.text((self.buttons[8][0] + width, self.buttons[8][1] + 30), u'C')
      # Return
      self.canvas.text((self.buttons[10][0] + 10, self.buttons[10][1] + 30), u'Return')
    
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
    
    elif event['scancode'] == key_codes.EScancodeBackspace:
      if event['type'] == appuifw.EEventKeyDown:
        text = '{"keyboard":{"keys":"BACKSPACE"}}\n'
    
    elif event['scancode'] == key_codes.EScancode0:
      if event['type'] == appuifw.EEventKeyDown:
        text = '{"keyboard":{"keys":"ENTER"}}\n'
    
    elif event['scancode'] == key_codes.EScancodeSelect:
      if self.mode != Mode.SCROLLING and event['type'] == appuifw.EEventKeyDown:
        self.mode = Mode.SGESTURE
        self.sleeper.after(0.2, self.resume)
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
    elif event['scancode'] == key_codes.EScancodeEdit:
      prev = self.mode
      self.mode = Mode.KEYBOARD
      self.locker.signal()
      appuifw.app.orientation = 'automatic'
      try:
        text = parse_string(appuifw.query(u'Text', 'text'))
      except:
        text = ''
      self.mode = prev
      appuifw.app.orientation = 'portrait'
      self.locker.signal()
      
    elif event['scancode'] == key_codes.EScancodeNo:
      appuifw.app.set_exit()
      return
    else:
      return
    
    text += "}}\n"

    # Supress clicks in scrolling and gesture mode
    if self.mode != Mode.MOUSE:
      return
    
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
