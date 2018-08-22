package com.example.paddlingsensor.Model.Queue;

import java.util.LinkedList;

/**
 * Created by Asad Hussain.
 */

public class IMUQueue<E> extends LinkedList<IMUQueueModel> {

    private int maxSize;
    private LinkedList<IMUQueueModel> list;

    public IMUQueue(int maxSize) {
        this.maxSize = maxSize;
        this.list = new LinkedList<>();
    }

    @Override
    public boolean add(IMUQueueModel object) {
        list.add(object);
        if (list.size() > this.maxSize) {
            list.removeFirst();

//            System.out.println("Removed one, current one: accX: "  + list.getFirst().getAccX() + " accY: " + list.getFirst().getAccY() + " accZ: " +
//            list.getFirst().getAccZ() + " gyrX: " + list.getFirst().getGyrX());
        }


        return true;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public IMUQueueModel peekLast() {
        if (list.size() == 0) {
            return null;
        }

        IMUQueueModel first = list.peekLast();
        return first;
    }
}
