package org.pentaho.tiger.mqtt;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class TigerMqttClient {
    static String TIMEVAL_STRING = "8/28/2017 1:38:05 PM";
    static SimpleDateFormat TIMEVAL_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
    static String LOCALDATE_STRING = "2017-08-28T13:38:05-05:00";
    static SimpleDateFormat LOCALDATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssX");

    static int INFINITE = -1;

    private String host = "localhost";
    private int port = 1883;
    private int numOfPub = 1;
    private int numOfMsg = 100;
    private int interval = 30;//second
    //Note this is MQTT topic, not Kafka topic
    private String topic;

    private Date timeValBase, localDateBase;

    public static void main(String args[]) {
        TigerMqttClient mc = new TigerMqttClient();
        String topic = null;
        try {
            for (int i = 0; i < args.length; i++) {
                if ("--host".equals(args[i])) {
                    mc.setHost(args[++i]);
                } else if ("--port".equals(args[i])) {
                    mc.setPort(Integer.parseInt(args[++i]));
                } else if ("--nop".equals(args[i])) {
                    mc.setNumOfPub(Integer.parseInt(args[++i]));
                } else if ("--nom".equals(args[i])) {
                    mc.setNumOfMsg(Integer.parseInt(args[++i]));
                } else if ("--topic".equals(args[i])) {
                    topic = args[++i];
                    if (topic == null || topic.trim().length() == 0) {
                        throw new Exception("Kafka topic is invalid");
                    }
                    mc.setTopic(topic);
                } else if ("--interval".equals(args[i])) {
                    mc.setInterval(Integer.parseInt(args[++i]));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Invalid arguments: " + ex.getMessage());
            System.exit(0);
        }

        if (topic == null) {
            System.out.println("Need topic");
            System.exit(0);
        }

        mc.publish();
    }

    public TigerMqttClient() {
        try {
            timeValBase = TIMEVAL_FORMAT.parse(TIMEVAL_STRING);
        } catch (Exception ex) {
            timeValBase = new Date();
        }

        try {
            localDateBase = LOCALDATE_FORMAT.parse(LOCALDATE_STRING);
        } catch (Exception ex) {
            localDateBase = new Date();
        }

    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setNumOfPub(int numOfPub) {
        this.numOfPub = numOfPub;
    }

    public void setNumOfMsg(int numOfMsg) {
        this.numOfMsg = numOfMsg;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void publish() {
        long total = 0;

        Thread[] threads = new Thread[numOfPub];
        Publisher[] publishers = new Publisher[numOfPub];

        for (int i = 0; i < numOfPub; i++) {
            final Publisher p = new Publisher(i);
            publishers[i] = p;

            Thread t = new Thread() {
                public void run() {
                    p.publish();
                }
            };

            threads[i] = t;
            t.start();
            try {
                //Sleep random time to start next
                Thread.sleep(new Random().nextInt(10) * 1000);
            } catch (Exception ex) {
            }
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        for (Publisher p : publishers) {
            total += p.totalBytes;
        }

        System.out.println("Total bytes sent: " + total);
    }

    class Publisher {
        private int id;
        private int count;
        private long totalBytes;

        public Publisher(int id) {
            this.id = id;
        }

        public long publish() {
            try {
                String brokerUrl = String.format("tcp://%s:%d", host, port);
                System.out.println("[" + id + "], broker url: " + brokerUrl);

                MqttClient client = new MqttClient(brokerUrl, MqttClient.generateClientId());
                client.connect();

                while (true) {
                    MqttMessage message = new MqttMessage();
                    String payload = dummy(id);
                    byte[] b = payload.getBytes();
                    totalBytes += b.length;
                    message.setPayload(b);

                    count++;
                    System.out.println("[" + id + "] sending message (" + (count) + "): " + payload);
                    client.publish(topic, message);

                    if (numOfMsg != INFINITE && count >= numOfMsg) {
                        break;
                    }

                    if (interval > 0) {
                        try {
                            Thread.sleep(interval * 1000);
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                }

                client.disconnect();
            } catch (MqttException me) {
                System.out.println(me.getMessage());
            }

            return totalBytes;
        }
    }

    private String dummy(int id) {
        Random r = new Random();
        SmartData sd = new SmartData();
        sd.value = 336438.125 + randomDouble(r, 1000);
        sd.integral = 1168.1879340277776 + randomDouble(r, 100);
        sd.riseTime = 0;
        sd.maxVal = 337667.09375 + randomDouble(r, 100);
        sd.declineTime = 0;
        sd.minVal = randomDouble(r, 100);
        sd.threshold = 100;
        sd.counts = randomInt(r, 10, 1);
        sd.countsDuration = 0.006944444444444444 + randomDouble(r, 1) / 100;
        sd.frequencyContent = 0;
        sd.waveFormShape = 0;

        PumpData pd = new PumpData();
        pd.smartData = sd;

        pd.scadaId = "00000000-0000-0000-" + String.format("%04d", id) + "-000000000000";
        pd.tagName = "JACK_ST_MALO.tot_linepack";
        pd.timeVal = TIMEVAL_FORMAT.format(new Timestamp(timeValBase.getTime() + randomInt(r, 1000)));
        pd.intVal = 0;
        pd.value = 336438.125 + randomDouble(r, 10);
        pd.modelVal = 0;
        pd.localDate = LOCALDATE_FORMAT.format(new Timestamp(localDateBase.getTime() + randomInt(r, 1000)));
        pd.valString = null;
        pd.localDateChanged = "0001-01-01T00:00:00";
        pd.timeValChanged = null;
        pd.valChanged = 0;
        pd.totalCount = randomInt(r, 10);
        pd.totalCountZero = randomInt(r, 100);
        pd.runTime = 0;
        pd.noicePercent = 1;

        return new Gson().toJson(pd);
    }

    private double randomDouble(Random r, int factor) {
        //subtract 0.5 to generate negative number
        return (r.nextDouble() - 0.5) * factor;
    }

    private int randomInt(Random r, int factor) {
        return r.nextInt() * factor;
    }

    private int randomInt(Random r, int base, int factor) {
        return r.nextInt(base) * factor;
    }

    class PumpData {
        @SerializedName("SCADA_ID")
        String scadaId;
        @SerializedName("tarname")
        String tagName;
        String timeVal;
        @SerializedName("intval")
        int intVal;
        double value;
        double modelVal;
        String localDate;
        String valString;
        String localDateChanged;
        String timeValChanged;
        double valChanged;
        int totalCount;
        int totalCountZero;
        double runTime;
        int noicePercent;
        SmartData smartData;
    }

    class SmartData {
        @SerializedName("Value")
        double value;
        @SerializedName("Integral")
        double integral;
        @SerializedName("RiseTime")
        double riseTime;
        @SerializedName("MaxVal")
        double maxVal;
        @SerializedName("DeclineTime")
        int declineTime;
        @SerializedName("MinVal")
        double minVal;
        @SerializedName("Threshold")
        int threshold;
        @SerializedName("Counts")
        int counts;
        @SerializedName("CountsDuration")
        double countsDuration;
        @SerializedName("FrequencyContent")
        double frequencyContent;
        @SerializedName("WaveFormShape")
        double waveFormShape;
    }
}
