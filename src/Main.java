import java.io.*;
import java.nio.file.*;
import java.util.*;
import static java.nio.file.StandardOpenOption.*;

public class Main {
    public static void main(String[] args) {
        try {
            //Reading input file path from command line arguments
            String path = args[0];
            Path file = Paths.get(path);
            //List to store the requests in the input file
            List<String> requests = new ArrayList<>();
            //fileCheck is to check file exists or not
            boolean fileCheck =false;
            //Reading the requests from the input file
            try (InputStream in = Files.newInputStream(file); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requests.add(line);
                }
            }
            catch (IOException e) {
                System.err.println(e);
                fileCheck = true;
            }
            //Instantiating the MovieTheater class and calling the processRequests() method with the requests
            MovieTheater movieTheater = new MovieTheater();
            List<String> outputs = movieTheater.processRequests(requests);
            String concatString = "";
            for (String output : outputs) {
                concatString += output + "\n";
            }
            if(!fileCheck) {
                byte[] data = concatString.getBytes();
                path = path.replaceAll("\\.[a-z]+", "-output.txt");
                System.out.println("The output for the program is found at : ");
                System.out.println(path);
                Path p = Paths.get(path);
                //Writing the reservations to the output file
                try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(p, CREATE, APPEND))) {
                    out.write(data, 0, data.length);
                } catch (IOException exception) {
                    System.err.println(exception);
                }
            }
        }
        catch (IndexOutOfBoundsException e){
            System.err.println("Please check the file, file is empty");
        }
        catch (Exception exception){
            System.err.println(exception);
        }
    }
}
