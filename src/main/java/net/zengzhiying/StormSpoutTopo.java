package net.zengzhiying;
import org.apache.storm.tuple.Fields;
import net.zengzhiying.WordCounter;
import net.zengzhiying.WordNormalizer;
import net.zengzhiying.RandomSentenceSpout;
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

public class StormSpoutTopo {
  public static class SplitSentence extends ShellBolt implements IRichBolt
 	{

    		public SplitSentence() {
      		//super("python", "splitsentence.py");
      		super("/bin/sh", "bolt.sh");
    		}

    	@Override
    	public void declareOutputFields(OutputFieldsDeclarer declarer) 		
    	{
      		declarer.declare(new Fields("word-cpp"));
    	}

    	@Override
    	public Map<String, Object> getComponentConfiguration() {
      	return null;
    	}
  	}

    public static void main(String[] args) {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout1", new RandomSentenceSpout());
        builder.setBolt("split1", new SplitSentence(), 2).shuffleGrouping("spout1");
        builder.setBolt("word-counter1", new WordCounter(),2)
                        .fieldsGrouping("split1", new Fields("word-cpp"));
        Config conf = new Config();
        conf.setDebug(true); 
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
            cluster.submitTopology("lg", conf, builder.createTopology());
            Utils.sleep(1000000);
            cluster.killTopology("lg");
            cluster.shutdown();
        }
        
        
        
    }
}
