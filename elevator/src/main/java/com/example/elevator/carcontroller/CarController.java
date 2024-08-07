package com.example.elevator.carcontroller;

import com.example.elevator.car.Direction;
import com.example.elevator.car.Display;
import com.example.elevator.car.ElevatorCar;
import com.example.elevator.user.User;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class CarController {

    ElevatorCar elevatorCar;

    PriorityQueue<User> minPQ = new PriorityQueue<>((User a, User b) -> a.getDesFloor() - b.getDesFloor());
    PriorityQueue<User> maxPQ = new PriorityQueue<>((User a, User b) -> b.getDesFloor() - a.getDesFloor());
    Queue<User> pendingQueue = new LinkedList<>();

    public CarController(ElevatorCar elevatorCar, PriorityQueue<User> minPQ, PriorityQueue<User> maxPQ) {
        this.elevatorCar = elevatorCar;
        this.minPQ = minPQ;
        this.maxPQ = maxPQ;
    }

    public void submitRequest(User user){
        if (canProcess(user.getCurrFloor(), user.getDesFloor())){
            if (Direction.UP == elevatorCar.getDisplay().getDirection()){
                minPQ.add(user);
            } else {
                maxPQ.add(user);
            }
        } else {
            pendingQueue.add(user);
        }

        controlElevatorCar();
    }

    public void controlElevatorCar(){
        if (elevatorCar.getDisplay().getDirection() == Direction.UP){
            System.out.println("processing UP requests of size: " + minPQ.size());
            while(!minPQ.isEmpty()){
                User user = minPQ.poll();
                elevatorCar.move(user.getCurrFloor(), user.getDesFloor());
                System.out.println("moved user to floor: " + user.getDesFloor());
            }

            while (!pendingQueue.isEmpty()){
                User user = pendingQueue.poll();
                Direction direction = getDirection(user);
                if (direction == Direction.UP){
                    minPQ.add(user);
                } else {
                    maxPQ.add(user);
                }
            }

            Display display = elevatorCar.getDisplay();
            display.setDirection(Direction.DOWN);
            elevatorCar.setDisplay(display);
        } else {
            System.out.println("processing Down requests of size: " + maxPQ.size());
            while(!maxPQ.isEmpty()){
                User user = maxPQ.poll();
                elevatorCar.move(user.getCurrFloor(), user.getDesFloor());
                System.out.println("moving user to floor: " + user.getDesFloor());
            }

            while (!pendingQueue.isEmpty()){
                User user = pendingQueue.poll();
                Direction direction = getDirection(user);
                if (direction == Direction.UP){
                    minPQ.add(user);
                } else {
                    maxPQ.add(user);
                }
            }

            Display display = elevatorCar.getDisplay();
            display.setDirection(Direction.UP);
            elevatorCar.setDisplay(display);
        }
    }

    public Direction getDirection(User user){
        if (user.getDesFloor() > user.getCurrFloor()){
            return Direction.UP;
        } else {
            return Direction.DOWN;
        }
    }

    public boolean canProcess(int curFloor, int desFloor){
        Display display = elevatorCar.getDisplay();
        Direction goToDirection = Direction.UP;
        if (desFloor < curFloor){
            goToDirection = Direction.DOWN;
        }

        return display.getFloor() < curFloor && display.getDirection() == goToDirection;
    }
}
