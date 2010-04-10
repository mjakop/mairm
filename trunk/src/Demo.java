
import java.awt.Robot;
import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;

import com.intel.bluetooth.BluetoothStack;
import com.intel.bluetooth.obex.BlueCoveOBEX;

import lib.*;

class Poslusalec implements DiscoveryListener {

			Vector<RemoteDevice> naprave=new Vector<RemoteDevice>();
			Vector<ServiceRecord> service=new Vector<ServiceRecord>();
	
            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
            	naprave.add(btDevice);
            	System.out.println("Nasel napravo.");
            }

            public void inquiryCompleted(int discType) {
            }

            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
                for (int i = 0; i < servRecord.length; i++) {
                	service.add(servRecord[i]);

                    String url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                    if (url == null) {
                        continue;
                    }
                    DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
                    if (serviceName != null) {
                        System.out.println("service " + serviceName.getValue() + " found " + url);
                    } else {
                        System.out.println("service found " + url);
                    }

                }
            }

            public void serviceSearchCompleted(int transID, int respCode) {
            }

}

public class Demo {
	
	public static void main(String[] args) throws Exception{
Poslusalec po=new Poslusalec();
LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, po);
Thread.sleep(7000);
UUID serviceUUID = new UUID(0x0003); //obex file transfer
UUID[] searchUuidSet = new UUID[] { serviceUUID };
int[] attrIDs =  new int[] {
        0x0100 // Service name
};


for (RemoteDevice naprava: po.naprave) {
	LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(null,searchUuidSet,naprava,po);
}
Thread.sleep(3000);
System.out.println("Povezal se bom.");
for (ServiceRecord serv: po.service) {
try {
	ClientSession clientSession = (ClientSession) Connector.open("btspp://001C9A246CDD:5;authenticate=false;encrypt=false;master=false");
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
}
		System.out.println(MAIRObject.version());
		MAIR m=new MAIR();
		//with input we specify from where lib should read data
		MAIRInput input=new MAIRInputComPort();
		m.setInput(input);
		m.start();
		//or
		m.startLearnMode();
		Thread.sleep(1000);
		m.stop();
		System.out.println("Finished");
	}
}
