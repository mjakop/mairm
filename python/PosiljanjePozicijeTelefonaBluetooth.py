import e32
import sensor,btsocket,appuifw, key_codes


def bt_povezi():
	global sock
	sock=btsocket.socket(btsocket.AF_BT,btsocket.SOCK_STREAM)
	target=''
	address,services=btsocket.bt_discover()
	if len(services)>1:
            import appuifw
            choices=services.keys()
            choices.sort()
            choice=appuifw.popup_menu([unicode(services[x])+": "+x
                                        for x in choices],u'Choose port:')
            target=(address,services[choices[choice]])
        else:
            target=(address,services.values()[0])
    	print "Povezujem..."+str(target)
    	sock.connect(target)
    	print "OK."


def sensor_event(data):
	global sock
	text=""+str(data["data_1"]/4)+";"+str(data["data_2"]/4)+";"+str(data["data_2"]/4)+";\n\r"
	sock.send(text)

def keypress(event):
	if event["scancode"]==key_codes.EScancodeLeftSoftkey:
		if event["type"] == appuifw.EEventKeyDown:
			text="#levDOL\n\r"
		else:
			text="#levGOR\n\r"
	sock.send(text)
		

bt_povezi()
sensors = sensor.sensors()
acc = sensor.Sensor(sensors["AccSensor"]["id"], sensors["AccSensor"]["category"])
acc.connect(sensor_event)
canvas = appuifw.Canvas(event_callback = keypress)
appuifw.app.body = canvas
e32.ao_sleep(15000)
acc.disconnect()