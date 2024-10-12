package app.backup;

import app.AppConfig;

import java.io.*;

public class DistributedFile implements Serializable {
    private final boolean isPublic; // Flag indicating if the file is public or private
    private final String filePath; // Path to the file
    private int ownerPort; // Port of the owner node
    private int ownerChordID; // Chord ID of the owner node

    // Constructor to initialize the distributed file with its properties
    public DistributedFile(String filePath, boolean isPublic, int ownerPort, int ownerChordID){
        this.isPublic = isPublic;
        this.filePath = filePath;
        this.ownerPort = ownerPort;
        this.ownerChordID = ownerChordID;
    }

    // Method to return a string representation of the distributed file
    @Override
    public String toString() {
        return (isPublic ? "[Public]" : "[Private]") + " " + filePath;
    }

    // Getter for the isPublic property
    public boolean isPublic() {
        return isPublic;
    }

    // Getter for the filePath property
    public String getFilePath() {
        return filePath;
    }

    // Getter for the ownerChordID property
    public int getOwnerChordID() {
        return ownerChordID;
    }

    // Getter for the ownerPort property
    public int getOwnerPort() {
        return ownerPort;
    }

    // Method to read and return the content of the file as a string
    public String getContent() {
        StringBuilder sb = new StringBuilder();
        File file = new File(filePath);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line = br.readLine()) != null){
                sb.append(line).append("\n");
            }
            br.close();
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint("Error while reading file: " + filePath);
            throw new RuntimeException(e);
        }
        return sb.toString();
    }
}
