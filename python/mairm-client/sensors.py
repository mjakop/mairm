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
