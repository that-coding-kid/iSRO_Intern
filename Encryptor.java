import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


class ClusterNode {
    int clusterAddress;
    ClusterNode next;

    public ClusterNode(int clusterAddress) {
        this.clusterAddress = clusterAddress;
        this.next = null;
    }
}

public class Encryptor {
    
    public static void FileSplitter(String filePath){


    }

    public static void Retrieval(String drivePath){


    }

    public static void DummyFiles(String folderPath, String filePath){
        File inputFile = new File(filePath);
        long criticalFileSize = inputFile.length();
        String fileExtension = getFileExtension(inputFile);

        int sectorSize = getSectorSize();
        int sectorsPerCluster = getSectorsPerCluster();

        int numberOfDummyFiles = calculateNumberOfDummyFiles(criticalFileSize, sectorSize, sectorsPerCluster);

        int dummyFileSize = (int)criticalFileSize/numberOfDummyFiles;
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        generateDummyFiles(dummyFileSize, folderPath, numberOfDummyFiles, fileExtension);


    }
    private static int calculateNumberOfDummyFiles(long criticalFileSize, int sectorSize, int sectorsPerCluster) {
        return (int) Math.ceil((double) criticalFileSize / (sectorsPerCluster * sectorSize));
    }

    private static void generateDummyFiles(int dummyFileSize, String folderPath, int numberOfFiles, String fileExtension) {
        for (int i = 2; i <= numberOfFiles; i+=2) {
            int fileNumber = i;
            String fileName = String.format("FILE%04d.%s", fileNumber, fileExtension);

            try {
                createDummyFile(dummyFileSize,folderPath, fileName);
                System.out.println("Generated file: " + fileName);
            } catch (IOException e) {
                System.err.println("Error creating file " + fileName + ": " + e.getMessage());
            }
        }
    }

    private static void createDummyFile(int dummyFileSize, String folderPath, String fileName) throws IOException {
        File file = new File(folderPath, fileName);
        try (FileWriter writer = new FileWriter(file)) {
            String content = "a".repeat(dummyFileSize * 1024);
            writer.write(content);
        }
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf + 1);
    }

    private static int getSectorSize() {
        // Assuming a common sector size, you might need to change this based on your environment
        return 512;
    }

    private static int getSectorsPerCluster() {
        // Assuming a common number of sectors per cluster, you might need to change this based on your environment
        return 8;
    }

    public static void AdditionalFiles(String folderPath, String filePath){
        File inputFile = new File(filePath);
        long criticalFileSize = inputFile.length();
        String fileExtension = getFileExtension(inputFile);

        int sectorSize = getSectorSize();
        int sectorsPerCluster = getSectorsPerCluster();

        int numFiles = calculateNumberOfDummyFiles(criticalFileSize, sectorSize, sectorsPerCluster);

        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        for (int i = 1; i <= numFiles+1; i+=2) {
            int fileNumber = i;
            String fileName = String.format("FILE%04d.%s", fileNumber, fileExtension);
            File file = new File(folderPath, fileName);

            try (FileWriter writer = new FileWriter(file)) {
                String content = "a".repeat((int)criticalFileSize/(int)numFiles * 1024);
                writer.write(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void CombineFiles(String externalDrivePath, String dummyFilesPath, String additionalFilesPath){        
        File dummyFilesFolder = new File(dummyFilesPath);
        File additionalFilesFolder = new File(additionalFilesPath);

        if (!dummyFilesFolder.exists() || !additionalFilesFolder.exists()) {
            System.err.println("Folders not found.");
            return;
        }

        // List files in the dummyFiles and additionalFiles folders
        List<File> dummyFiles = Arrays.asList(dummyFilesFolder.listFiles());
        List<File> additionalFiles = Arrays.asList(additionalFilesFolder.listFiles());

        // Sort files to ensure correct order
        dummyFiles.sort(Comparator.comparing(File::getName));
        additionalFiles.sort(Comparator.comparing(File::getName));

        // Combine files in an alternating manner
        int maxFiles = Math.max(dummyFiles.size(), additionalFiles.size());
        int dummyIndex = 0, additionalIndex = 0;

        for (int i = 1; i < maxFiles * 2; i++) {
            if (i%2==1 && additionalIndex < additionalFiles.size()) {
                // Write additional file
                writeFileToExternalDrive(additionalFiles.get(additionalIndex++), externalDrivePath);
            } else if (dummyIndex < dummyFiles.size()) {
                // Write dummy file
                writeFileToExternalDrive(dummyFiles.get(dummyIndex++), externalDrivePath);
            }
        }

    }

    private static void writeFileToExternalDrive(File file, String externalDrivePath) {
        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(externalDrivePath + file.getName())) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

        } catch (IOException e) {
            System.err.println("Error writing file: " + file.getName());
            e.printStackTrace();
        }
    }

    public static void physicalClusterReservation(String externalDrivePath){
        int rootDirSize = calcRootdir(externalDrivePath);
        ClusterNode head = FAT32ReadandDelete(externalDrivePath);
    }

    private static int calcRootdir(String externalDrivePath) {
        String filePath = externalDrivePath;
        int sectorSize = 512; // Typical sector size for FAT

        try (RandomAccessFile device = new RandomAccessFile( filePath, "r")) {
            // Read the boot sector
            byte[] bootSector = new byte[sectorSize];
            device.readFully(bootSector);

            // Extract necessary information from the boot sector
            int reservedSectors = ((bootSector[15] & 0xFF) << 8) | (bootSector[14] & 0xFF);
            int numberOfFATs = bootSector[16] & 0xFF;
            int sectorsPerFAT = ((bootSector[39] & 0xFF) << 24) |
                                ((bootSector[38] & 0xFF) << 16) |
                                ((bootSector[37] & 0xFF) << 8) |
                                (bootSector[36] & 0xFF);
            int rootCluster = ((bootSector[47] & 0xFF) << 24) |
                              ((bootSector[46] & 0xFF) << 16) |
                              ((bootSector[45] & 0xFF) << 8) |
                              (bootSector[44] & 0xFF);
            int sectorsPerCluster = bootSector[13] & 0xFF;

            // Start of the first FAT table
            int fatStartSector = reservedSectors;
            int fatSize = sectorsPerFAT * sectorSize;

            // Read the first FAT copy
            device.seek(fatStartSector * sectorSize);
            byte[] fat = new byte[fatSize];
            device.readFully(fat);

            // Traverse the cluster chain for the root directory
            int cluster = rootCluster;
            int clusterCount = 0;

            while (cluster < 0x0FFFFFF8) { // End of cluster chain for FAT32
                clusterCount++;
                int fatIndex = cluster * 4;
                cluster = ((fat[fatIndex + 3] & 0xFF) << 24) |
                          ((fat[fatIndex + 2] & 0xFF) << 16) |
                          ((fat[fatIndex + 1] & 0xFF) << 8) |
                          (fat[fatIndex] & 0xFF);
                cluster &= 0x0FFFFFFF; // Mask to get only 28 bits as FAT32 uses 28 bits for cluster addresses
            }

            // Calculate the size of the root directory
            return clusterCount * sectorsPerCluster * sectorSize/1024;

            //System.out.printf("Root directory size: %d bytes%n", rootDirSize);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static ClusterNode FAT32ReadandDelete(String externalDrivePath){
        int sectorSize = 512; // Typical sector size for FAT

        try (RandomAccessFile device = new RandomAccessFile(externalDrivePath, "r")) {
            // Read the boot sector
            byte[] bootSector = new byte[sectorSize];
            device.readFully(bootSector);

            // Extract necessary information from the boot sector
            int reservedSectors = ((bootSector[15] & 0xFF) << 8) | (bootSector[14] & 0xFF);
            int numberOfFATs = bootSector[16] & 0xFF;
            int sectorsPerFAT = ((bootSector[39] & 0xFF) << 24) |
                                ((bootSector[38] & 0xFF) << 16) |
                                ((bootSector[37] & 0xFF) << 8) |
                                (bootSector[36] & 0xFF);

            // Start of the first FAT table
            int fatStartSector = reservedSectors;
            int fatSize = sectorsPerFAT * sectorSize;

            // Read the first FAT copy
            device.seek(fatStartSector * sectorSize);
            byte[] fat = new byte[fatSize];
            device.readFully(fat);

            // Read the root directory (for simplicity, assuming it's located after the FAT area)
            int rootDirStartSector = fatStartSector + numberOfFATs * sectorsPerFAT;
            
            int rootDirSize = calcRootdir(externalDrivePath) * 1024; // 32 KB for the root directory
            //System.out.println(rootDirSize);
            byte[] rootDir = new byte[rootDirSize];
            device.seek(rootDirStartSector * sectorSize);
            device.readFully(rootDir);

            // Process the root directory entries
            ClusterNode head = null;
            ClusterNode current = null;

            for (int i = 128; i < rootDir.length; i += 32) {
                // Read a directory entry
                String fileName = new String(rootDir, i, 11).trim();
                int startingCluster = ((rootDir[i + 21] & 0xFF) << 16) |
                      ((rootDir[i + 27] & 0xFF) << 8) |
                      (rootDir[i + 26] & 0xFF);
                //String fileNumberStr = fileName.substring(5, fileName.length() - 5);
                System.out.println(fileName);

                // Check if the file name matches the pattern "File-(file_Number).txt"
                if (fileName.matches("FILE\\d{4}"+"TXT")) {
                    // Extract the file number
                    String fileNumberStr = fileName.substring(5, 8);
                    System.out.println(fileNumberStr);
                    int fileNumber = Integer.parseInt(fileNumberStr);

                    //System.out.printf("Processing file: %s, Starting cluster: %d%n", fileName, startingCluster);

                    // Check if the file number is even
                    if (fileNumber % 2 == 0) {
                        // Follow the cluster chain in the FAT table
                        int cluster = startingCluster;

                        while (cluster < 0x0FFFFFF8) { // End of cluster chain for FAT32
                            //System.out.printf("Cluster address: %08X%n", cluster);

                            // Create a new node for the cluster address
                            ClusterNode newNode = new ClusterNode(cluster);

                            if (head == null) {
                                head = newNode;
                                current = head;
                            } else {
                                current.next = newNode;
                                current = newNode;
                            }

                            // Get the next cluster from the FAT table
                            int fatIndex = cluster * 4;
                            cluster = ((fat[fatIndex + 3] & 0xFF) << 24) |
                                      ((fat[fatIndex + 2] & 0xFF) << 16) |
                                      ((fat[fatIndex + 1] & 0xFF) << 8) |
                                      (fat[fatIndex] & 0xFF);

                            // Mask to get only 28 bits as FAT32 uses 28 bits for cluster addresses
                            cluster &= 0x0FFFFFFF;
                        }
                    }
                }
            }
            
            System.out.println("Linked list of clusters for files ending with an even number:");
            ClusterNode temp = head;
            while (temp != null) {
                System.out.printf("Cluster address: %08X%n", temp.clusterAddress);
                temp = temp.next;
            }
            temp = head;
            while (temp != null) {
                int fatIndex = temp.clusterAddress * 4;
                fat[fatIndex] = 0x00;
                fat[fatIndex + 1] = 0x00;
                fat[fatIndex + 2] = 0x00;
                fat[fatIndex + 3] = 0x00;
                temp = temp.next;
            }
    
            // Write the updated FAT table back to the device
            device.seek(fatStartSector * sectorSize);
            device.write(fat);
    
            System.out.println("Marked clusters as free and updated FAT table.");
            return temp;
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return null;

    }

    

}
