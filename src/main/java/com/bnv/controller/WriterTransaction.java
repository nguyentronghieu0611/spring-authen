package com.bnv.controller;

class WriterTransaction extends Thread {
    public void run() {
        long startTime = System.currentTimeMillis();
        int i = 0;
        while (true) {
            System.out.println(this.getName() + ": New Thread is running..." + i++);
            try {
                //Wait for one sec so it doesn't print too fast
                if(i==5 || i==8)
                    Thread.sleep(500);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(i==10)
                break;;
        }
    }
}
