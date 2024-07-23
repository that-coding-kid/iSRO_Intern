import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class readSector {

    public static byte[] readSector(String drive, int sectorIndex) {
        int sectorSize = 512; // Assuming sector size is 512 bytes
        byte[] sectorData = new byte[sectorSize];
        String filePath = "\\\\.\\" + drive + ":"; // Accessing the raw drive

        try (RandomAccessFile driveFile = new RandomAccessFile(filePath, "r")) {
            long position = (long) sectorIndex * sectorSize;
            driveFile.seek(position);
            driveFile.readFully(sectorData);

        } catch (FileNotFoundException e) {
            System.err.println("Drive not found: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.err.println("Error reading sector: " + e.getMessage());
            return null;
        }
        return sectorData;
    }

    
    public static byte[] readBySize(String drive, int sectorIndex, int length) {
        int sectorSize = length; // Assuming sector size is 512 bytes
        byte[] sectorData = new byte[sectorSize];
        String filePath = "\\\\.\\" + drive + ":"; // Accessing the raw drive

        try (RandomAccessFile driveFile = new RandomAccessFile(filePath, "r")) {
            long position = (long) sectorIndex * sectorSize;
            driveFile.seek(position);
            driveFile.readFully(sectorData);

        } catch (FileNotFoundException e) {
            System.err.println("Drive not found: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.err.println("Error reading sector: " + e.getMessage());
            return null;
        }
        return sectorData;
    }

    public static void main(String[] args) {
        String drive = "D"; // Example drive
        int sectorIndex = 0; // Example sector index

        byte[] sectorData = readSector(drive, sectorIndex);
        if (sectorData != null) {
            System.out.println("Sector Data:");
            for (byte b : sectorData) {
                System.out.printf("%02X ", b);
            }
        } else {
            System.out.println("Failed to read sector.");
        }
    }
}
