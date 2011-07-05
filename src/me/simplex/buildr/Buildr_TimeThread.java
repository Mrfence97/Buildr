package me.simplex.buildr;

import org.bukkit.World;

public class Buildr_TimeThread implements Runnable {
	Buildr plugin;
	boolean alive;



	public Buildr_TimeThread(Buildr plugin) {
		this.plugin = plugin;
		this.alive=true;
	}


	@Override
	public void run() {
		plugin.log("started TimeThread.");
		while(isAlive()){
			try {
				Thread.sleep(30000); //sleep 30sec
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		for (World world : plugin.getWorldbuildmode()) {
			if (world.getTime()>=10000) {
				world.setTime(0);
				plugin.log("time reset");
			}
		}
		plugin.log("Time checked");
		}
		plugin.log("stopped TimeThread.");
	}
	public boolean isAlive() {
		return alive;
	}


	public void setAlive(boolean alive) {
		this.alive = alive;
	}

}
