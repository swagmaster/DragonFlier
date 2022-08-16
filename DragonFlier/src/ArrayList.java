 

import java.util.Arrays;

public class ArrayList {

    //class variables
    private int numElements = 0;
    private int currCap = 100;
    private Object[] list = new Object[100];

    /*
     * insert() method inserts an object at an indicated index.
     */
    public void insert(Object obj, int index){
        //extends the ArrayList if current capacity is reached
        if(numElements == list.length){
            currCap*=2;
            list = Arrays.copyOf(list, currCap);
        }

        //bumps all current elements up 1 index to make room for new object
        for(int i = numElements; i > index; i--){
            list[i] = list[i-1];
        }
        //adds object
        list[index] = obj;
        numElements++;//tracks number of elements
    }
    /*
     * add() method adds the object to the end of the list
     */
    public void add(Object obj){
        //expands the ArrayList if the current capacity is reached
        if(numElements == list.length){
            currCap*=2;
            list = Arrays.copyOf(list, currCap);
        }
        //adds the object to the farthest index
        list[numElements] = obj;
        numElements++;
    }
    /*
     * remove() method removes the object at the indicated index and returns
     * that object
     */
    public Object remove(int index){
        Object item = list[index];
        for(int i = index; i<numElements; i++){
            list[i] = list[i+1];
        }
        numElements--;
        return item;
    }
    /*
     * size() gets the size of the array list
     */
    public int size(){
        return numElements;
    }
    public boolean isEmpty(){
        boolean check = true;
        for(int i = 0; i<numElements; i++){
            if(list[i] != null){
                check = false;
            }
        }
        return check;
    }
    
    /*
     * indexOf() gets the index of an object if it exists
     * in the array list or not.
     */
    public int indexOf(Object obj){
        int index = -1;
        for(int i = 0; i < numElements; i++){
            if(obj.equals(list[i])){
                index = i;
                break;
            }
        }
        return index;
    }
    /*
     * toString() method puts the objects into a nice string format
     */
    public String toString(){
        String arrayList = "";
        for(int i = 0; i < numElements; i++){
            //adds all objects to string followed by a new line
            arrayList += list[i].toString() + "\n";
        }

        return arrayList;
    }
    /*
     * equals() method checks to see if another arraylist 
     * is the same as the current one
     */
    public boolean equals(Object obj){
        ArrayList that = (ArrayList) obj; //type cast
        boolean check = true;
        if(this.numElements != that.size()){//if ArrayLists have different sizes
            check = false;
        }
        for(int i = 0; i < this.numElements; i++){
            if(this.get(i) != that.get(i)){ //if elements don't equal
                check = false;
            }
        }
        return check;
    }
    /*
     * get() gets the object at a indicated index.
     */
    public Object get(int index){
        return list[index];
    }
} 