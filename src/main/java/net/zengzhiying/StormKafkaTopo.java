package net.zengzhiying;
import org.apache.storm.tuple.Fields;
import net.zengzhiying.WordCounter;
import net.zengzhiying.WordNormalizer;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.task.ShellBolt;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.utils.Utils;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.bolt.KafkaBolt;

public class StormKafkaTopo {
  public static class SplitSentence extends ShellBolt implements IRichBolt
 	{

    		public SplitSentence() {
      		//super("python", "splitsentence.py");
      		super("/bin/sh", "bolt.sh");
    		}

    	@Override
    	public void declareOutputFields(OutputFieldsDeclarer declarer) 		
	{
      		declarer.declare(new Fields("word"));
    	}

    	@Override
    	public Map<String, Object> getComponentConfiguration() {
      	return null;
    	}
  	}

    public static void main(String[] args) {
        BrokerHosts brokerHosts = new ZkHosts("127.0.0.1:2181");
        
        SpoutConfig spoutConfig = new SpoutConfig(brokerHosts, "topic1", "", "kafkaspout");
        
        Config conf = new Config();
        Map<String, String> map = new HashMap<String, String>();
        
        map.put("metadata.broker.list", "127.0.0.1:9092");
        map.put("serializer.class", "kafka.serializer.StringEncoder");
        conf.put("kafka.broker.properties", map);
        conf.put("topic", "topic2");
        
        spoutConfig.scheme = new SchemeAsMultiScheme(new MessageScheme());
        
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout", new KafkaSpout(spoutConfig));
	builder.setBolt("split", new SplitSentence(), 8).shuffleGrouping("spout");
	//builder.setBolt("word-normalizer", new WordNormalizer())
          //              .shuffleGrouping("split");
        builder.setBolt("word-counter", new WordCounter(),1)
                        .fieldsGrouping("split", new Fields("word"));
/*
        builder.setBolt("boltredis", new RedisTest()).shuffleGrouping("spout");
        builder.setBolt("bolt", new SenqueceBolt()).shuffleGrouping("boltredis");
        builder.setBolt("kafkabolt", new KafkaBolt<String, Integer>()).shuffleGrouping("bolt");
  */      
        if(args != null && args.length > 0) {
            //提交到集群运行
            try {
                StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
            } catch (AlreadyAliveException e) {
                e.printStackTrace();
            } catch (InvalidTopologyException e) {
                e.printStackTrace();
            } catch (AuthorizationException e) {
	    	e.printStackTrace();
	    }
	
	     
        } else {
            //本地模式运行
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("Topotest1121", conf, builder.createTopology());
            Utils.sleep(1000000);
            cluster.killTopology("Topotest1121");
            cluster.shutdown();
        }
        
        
        
    }
}
