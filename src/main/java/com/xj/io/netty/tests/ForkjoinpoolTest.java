package com.xj.io.netty.tests;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.TimeUnit;

public class ForkjoinpoolTest {

    /**
     * 定义一个可分解的任务类，继承了RecursiceAction抽象类
     * 必须实现它的compute方法
     */
    public static class MyTask extends RecursiveAction{

        //定义一个分解任务的阀值--50，既一个任务最多承担50个工作量
        final int THRESHOLD = 50;
        //任务量
        int task_num = 0;

        MyTask(int num){
            this.task_num = num;
        }

        @Override
        protected void compute() {
            if (task_num <= THRESHOLD){
                System.out.println(Thread.currentThread().getName()+"承担了"+task_num);
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }else{
                //随机解成两个任务
                Random m = new Random();
                int x = m.nextInt(50);

                MyTask left = new MyTask(x);
                MyTask right = new MyTask(task_num - x);

                left.fork();
                right.fork();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        //创建一个支持分解任务的线程池ForkJoinPool
        ForkJoinPool pool = new ForkJoinPool();
        MyTask task = new MyTask(178);

        pool.submit(task);
        pool.awaitTermination(20, TimeUnit.SECONDS);//等待20s，观察结果
        pool.shutdown();
    }
}
