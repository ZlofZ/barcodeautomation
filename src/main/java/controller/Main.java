package controller;

import controller.celesta.AppRunnerCelesta;
import controller.kaukokiito.AppRunnerKaukokiito;

public class Main{
	public static void main(String[] args){
		System.out.println("agrs len "+args.length);
	    new AppRunnerCelesta(args[0]);
	    //new AppRunnerKaukokiito(args[0]);
	}
}
