import sensor

class AbstractSensor:
  def coord(self):
    return (0, 0, 0)
  
  def coord_cb(self):
    pass
  
  def set_callback(self, cb):
    pass

class OldSensor(AbstractSensor):
  def __init__(self):
    sensors = sensor.sensors()
    self.sensor = sensor.Sensor(sensors["AccSensor"]["id"], sensors["AccSensor"]["category"])
    
    self.sensor.connect(self.coord_cb)
  
  def coord_cb(self, data):
    if self.cb is None:
      return

    self.cb(data["data_1"], data["data_2"], data["data_3"])
  
  def set_callback(self, cb):
    self.cb = cb