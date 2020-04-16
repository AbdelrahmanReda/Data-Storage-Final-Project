import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class Main {

    static void CreateIndexFile(String fileName, int numberOfRecords, int m) {
        try {
            int counter = 0;
            RandomAccessFile file = new RandomAccessFile(fileName, "rw");
            for (int i = 0; i < numberOfRecords; i++) {
                for (int j = 0; j < (m * 2) + 1; j++) {
                    if (j == 1) {
                        counter++;
                        file.writeInt(i + 1);
                    } else {
                        counter++;
                        file.writeInt(-1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static int SearchARecord(String filename, int RecordID) {
        try {
            RandomAccessFile file = new RandomAccessFile(filename, "rw");
            file.seek(11 * 4);
            while (true) {
                int detector = file.readInt();
                if (detector == 1) {
                    for (int i = 0; i < 11; i++) {
                        int reader = file.readInt();
                        if (reader >= RecordID) {
                            int x = file.readInt();
                            file.seek(x * 44);
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < 5; i++) {
                        int number = file.readInt();
                        if (number == RecordID && number != -1) {
                            return 0;
                        } else {
                            file.readInt();
                        }
                    }
                    return 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    static void DisplayIndexFileContent(String fileName) {
        try {
            RandomAccessFile file = new RandomAccessFile(fileName, "rw");
            for (int i = 1; i <= file.length() / 4; i++) {
                int number = file.readInt();
                if (number != -1) {
                    System.out.print("  " + number);
                } else {
                    System.out.print(" " + number);
                }

                if (i != 0 && i % 11 == 0)
                    System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void insertNode(RandomAccessFile file, int key, int ref, int pointer) {
        try {
            while ((int) file.getFilePointer() % 44 != 0) {
                int tempKey = file.readInt();    //
                int tempRef = file.readInt();    //
                file.seek(file.getFilePointer() - 8); //step 8 step backward
                file.writeInt(key);
                file.writeInt(ref);
                key = tempKey;
                ref = tempRef;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static int getNextFreeRecord() {
        try {
            RandomAccessFile file = new RandomAccessFile("src/index.bin", "rw");
            file.seek(4);
            return file.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void maintainFirstFreeRecord() throws IOException {
        try {
            RandomAccessFile file = new RandomAccessFile("src/index.bin", "rw");
            file.seek(44);
            for (int i = 0; i < 9; i++) {
                if (file.readInt() == -1) {
                    updateFirstFreeRecord(file, i + 1);
                    break;
                } else
                    file.seek((file.getFilePointer() - 4) + 44);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void maintainIndexFile(int targetRecordIndex, int firstMax, int firstReference, int secondMax, int secondReference) throws IOException {
        String indexFileName = "src/index.bin";
        RandomAccessFile file = new RandomAccessFile("src/index.bin", "rw");
        if (targetRecordIndex == 1) {
            file.seek((targetRecordIndex * 44) + 4);
            file.writeInt(firstMax);
            file.writeInt(firstReference);
            file.writeInt(secondMax);
            file.writeInt(secondReference);
        }
    }

    public static void splitNode(RandomAccessFile file, int targetRecordIndex, ArrayList<Integer> visited, int key, int reference) throws IOException {
        ArrayList<Integer> arr = new ArrayList<>();
        boolean switcher = true;
        file.seek(targetRecordIndex * 44 + 4);
        for (int i = 0; i < 10; i += 2) {
            int currentNumber = file.readInt();
            file.seek(file.getFilePointer() - 4);
            file.writeInt(-1);
            int currentReference = file.readInt();
            file.seek(file.getFilePointer() - 4);
            file.writeInt(-1);

            if (key < currentNumber && switcher) {
                switcher = false;
                arr.add(key);
                arr.add(reference);
                arr.add(currentNumber);
                arr.add(currentReference);

            } else {
                arr.add(currentNumber);
                arr.add(currentReference);
            }
        }
        if (key > arr.get(arr.size() - 2)) {
            arr.add(key);
            arr.add(reference);
        }
        if (targetRecordIndex == 1) {
            file.seek(44);
            int number = file.readInt();
            file.seek(file.getFilePointer() - 4);
            int detector = 0;
            if (number == 1)
                detector = 1;

            file.writeInt(1); //convert the record to referential record . !will be replaced later
            int firstMax = -1;
            int secondMax = -1;
            int firstReference = getNextFreeRecord();
            file.seek(firstReference * 44);
            file.writeInt(detector); //convert the record to non referential record
            int lim = arr.size() / 2;
            for (int i = 0; i < lim; i++) {
                file.writeInt(arr.get(i));
                if (i % 2 == 0)
                    firstMax = arr.get(i);
            }
            maintainFirstFreeRecord();
            int secondReference = getNextFreeRecord();
            file.seek(secondReference * 44);
            file.writeInt(detector); //convert the record to non referential record
            for (int i = 0; i < arr.size() - lim; i++) {
                file.writeInt(arr.get(i + lim));
                if (i % 2 == 0)
                    secondMax = arr.get(i + lim);
            }
            maintainIndexFile(targetRecordIndex, firstMax, firstReference, secondMax, secondReference);
        } else {


            int firstMax = -1;
            int secondMax = -1;

            file.seek((targetRecordIndex * 44) + 4);
            for (int i = 0; i < 10; i++) {
                file.writeInt(-1);
            }


            file.seek((targetRecordIndex * 44) + 4);
            int lim = arr.size() / 2;
            for (int i = 0; i < lim; i++) {
                file.writeInt(arr.get(i));
                if (i % 2 == 0)
                    firstMax = arr.get(i);
            }
            int secondReference = getNextFreeRecord();
            file.seek((secondReference * 44));
            file.writeInt(0);

            for (int i = 0; i < arr.size() - lim; i++) {
                file.writeInt(arr.get(i + lim));
                if (i % 2 == 0)
                    secondMax = arr.get(i + lim);
            }


            for (int i = 0; i < visited.size(); i++) {
                file.seek(visited.get(i) - 4);
                file.writeInt(-1);
                file.writeInt(-1);
            }

            objecter objecter = new objecter();
            objecter.properRecord = 1;
            ArrayList<Integer> alves = new ArrayList<>();
            objecter.vistied = alves;
            maintainFirstFreeRecord();
            coordinateNode(objecter, firstMax, targetRecordIndex);
            coordinateNode(objecter, secondMax, secondReference);
        }
    }

    public static void updateNodeReference(ArrayList<Integer> visitedNodes, int maxId) throws IOException {
        RandomAccessFile file = new RandomAccessFile("src/index.bin", "rw");
        for (int i = 0; i < visitedNodes.size(); i++) {
            file.seek(visitedNodes.get(i) - 4);
            file.writeInt(maxId);
        }
    }

    public static void updateFirstFreeRecord(RandomAccessFile file, int nextFree) {
        try {
            file.seek(4);
            file.writeInt(nextFree);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void coordinateNode(objecter obj, int key, int reference) throws IOException {
        int recordNumber = obj.properRecord;
        RandomAccessFile file = new RandomAccessFile("src/index.bin", "rw");
        file.seek(((recordNumber + 1) * 44) - 4);
        //checking the last number of the record
        if (file.readInt() != -1) {
            splitNode(file, recordNumber, obj.vistied, key, reference);
        } else {
            file.seek((recordNumber * 44));
            if (file.readInt() == -1) {
                file.seek(file.getFilePointer() - 4);
                file.writeInt(0);
                file.writeInt(key);
                file.writeInt(reference);
            } else {
                while (true) {
                    int x = file.readInt();
                    if (key < x) {
                        //the new key is less than the key at this position
                        file.seek(file.getFilePointer() - 4); //rollback 4 bytes
                        //function call to cascade the rest of the record to the lift and add the new record
                        insertNode(file, key, reference, (int) (file.getFilePointer()));
                        break;
                    } else if (x == -1) {
                        file.seek(file.getFilePointer() - 4);
                        updateNodeReference(obj.vistied, key);
                        file.writeInt(key);
                        file.writeInt(reference);
                        break;
                    } else {
                        file.seek(file.getFilePointer() + 4);  //skip 4 byte of the reference
                    }
                }
            }
        }

    }

    public static void printSearchForProperRecord(objecter objecter) {
        System.out.println("objecter.vistied " + objecter.vistied);
        System.out.println("objecter.properRecord " + objecter.properRecord);
    }

    static int InsertNewRecordAtIndex(String filename, int RecordID, int Reference) {
        try {
            RandomAccessFile file = new RandomAccessFile(filename, "rw");
            file.seek(44);
            objecter objecter = searchForProperRecord(RecordID);
            //printSearchForProperRecord(objecter);
            coordinateNode(objecter, RecordID, Reference);
            maintainFirstFreeRecord();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static objecter searchForProperRecord(int RecordID) throws IOException {
        objecter obj = new objecter();
        ArrayList<Integer> visited = new ArrayList<>();
        RandomAccessFile file = new RandomAccessFile("src//index.bin", "r"); //read the file
        file.seek(11 * 4);
        while (true) {

            if (file.readInt() == 1) //its referential node
            {
                for (int i = 0; i < 10; i++) {
                    int reader = file.readInt();
                    if (reader == -1) {

                        file.seek((file.getFilePointer() - 8));
                        int y = file.readInt();
                        visited.add((int) file.getFilePointer() - 4);
                        file.seek(y * 44);
                        break;
                    }
                    if (reader != -1 && i + 1 == 10) {

                        visited.add((int) file.getFilePointer() - 4);
                        obj.vistied = visited;
                        file.seek(file.getFilePointer() - 4);
                        obj.properRecord = file.readInt();
                        return obj;
                    }
                    if (RecordID < reader) {
                        int c = file.readInt();
                        visited.add((int) file.getFilePointer() - 4);
                        file.seek(c * 44);
                        break;
                    }
                }
            } else {
                obj.vistied = visited;
                obj.properRecord = (int) ((file.getFilePointer() - 4) / 44);
                return obj;
            }
        }
    }

    public static void requiredTestCase() {
        String indexFileName = "src//index.bin";
        InsertNewRecordAtIndex(indexFileName, 3, 0);
        InsertNewRecordAtIndex(indexFileName, 7, 0);
        InsertNewRecordAtIndex(indexFileName, 10, 0);
        InsertNewRecordAtIndex(indexFileName, 24, 0);
        InsertNewRecordAtIndex(indexFileName, 14, 0);
        InsertNewRecordAtIndex(indexFileName, 19, 0);
        InsertNewRecordAtIndex(indexFileName, 30, 0);
        InsertNewRecordAtIndex(indexFileName, 15, 0);
        InsertNewRecordAtIndex(indexFileName, 1, 0);
        InsertNewRecordAtIndex(indexFileName, 5, 0);
        InsertNewRecordAtIndex(indexFileName, 2, 0);
        InsertNewRecordAtIndex(indexFileName, 8, 0);
        InsertNewRecordAtIndex(indexFileName, 9, 0);
        InsertNewRecordAtIndex(indexFileName, 6, 0);
        InsertNewRecordAtIndex(indexFileName, 11, 0);
        InsertNewRecordAtIndex(indexFileName, 12, 0);
        InsertNewRecordAtIndex(indexFileName, 17, 0);
        InsertNewRecordAtIndex(indexFileName, 18, 0);
        InsertNewRecordAtIndex(indexFileName, 32, 0);
    }

    public static boolean isUnderFlow(int RequidtedDeletedKeybyteOffset) throws IOException {
        RandomAccessFile file = new RandomAccessFile("src//index.bin", "rw");
        file.seek(RequidtedDeletedKeybyteOffset);
        int ByteOffsetcounter = RequidtedDeletedKeybyteOffset;
        while (ByteOffsetcounter % 44 != 0) {
            ByteOffsetcounter -= 4;
        }
        ByteOffsetcounter += 4;
        //System.out.println("ByteOffsetcounter"+((int)(((RequidtedDeletedKeybyteOffset-ByteOffsetcounter)/4)/2)+1));
        if (((int) (((RequidtedDeletedKeybyteOffset - ByteOffsetcounter) / 4) / 2) + 1) - 1 < (5 / 2))
            return false;
        else
            return true;

    }

    static void DeleteRecordFromIndex(String filename, int RecordID) {
        try {
            isUnderFlow(10 * 44 + 4 * 3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        String indexFileName = "src//index.bin";
        CreateIndexFile(indexFileName, 10, 5);
        requiredTestCase();
        DisplayIndexFileContent(indexFileName);
        DeleteRecordFromIndex(indexFileName, 10);


    }


}

