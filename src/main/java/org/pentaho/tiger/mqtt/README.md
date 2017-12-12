# Mosquitto is an open source message broker that supports MQTT 3.1 and 3.1.1
    https://mosquitto.org/
# Install Mosquitoo on Ubuntu
    sudo apt-get install mosquitto mosquitto-clients
# Environment to test MQTT message flow from MQTT client to Kafka topic  
    1. Install Kafka, Kafka connector by following this link:
        https://howtoprogram.xyz/2016/07/30/apache-kafka-connect-mqtt-source-tutorial/
    2. Create a local directory, say "tiger", download jar files from lib
        tiger-1.0-SNAPSHOT.jar
        org.eclipse.paho.client.mqttv3-1.1.0.jar
        org.eclipse.paho.client.mqttv3-1.1.0.jar
    3. Start Zookeeper, Kafka, and Kafka connector, (steps are described in step 1)
    4. Navigate into "tiger" directory created in step 2, run the MQTT client
    
        java -cp .:* org.pentaho.tiger.mqtt.tigerMqttClient --host localhost --port 1883 --nom -1 --nop 10 --topic hello-mqtt --interval 30
                
          --host    : host of mosquitto
          --port    : port of mosquitto
          --nom     : number of messages to send. -1 means no limit
          --nop     : number of publisher 
          --topic   : mosquitto topic (this is not Kafka topic)
          --interval: number of seconds that a publisher will sleep before sending next message
        
    