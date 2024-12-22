package com.example.testapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollegePredictorActivity extends AppCompatActivity {

    private TextView textViewResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_predictor);

        Button buttonPredictColleges = findViewById(R.id.button_predict_colleges);
        textViewResults = findViewById(R.id.text_view_results);

        buttonPredictColleges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Run the college prediction logic in a background thread
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String url = "https://example.com"; // Replace with the URL of the website to scrape
                            String pdfDirectory = getExternalFilesDir(null) + "/pdfs"; // Directory to save the downloaded PDFs

                            // Create the PDF directory if it doesn't exist
                            File pdfDir = new File(pdfDirectory);
                            if (!pdfDir.exists()) {
                                pdfDir.mkdirs();
                            }

                            // Scrape the website for PDF links
                            List<String> pdfLinks = scrapePdfLinks(url);
                            String mostRecentPdfLink = getMostRecentPdfLink(pdfLinks);
                            String pdfFilePath = downloadPdf(mostRecentPdfLink, pdfDirectory);
                            String pdfText = extractTextFromPdf(pdfFilePath);
                            String results = processPdfText(pdfText);

                            // Update the UI with the results
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textViewResults.setText(results);
                                }
                            });

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    // Function to scrape the website for PDF links
    private List<String> scrapePdfLinks(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        List<String> pdfLinks = new ArrayList<>();

        for (Element link : links) {
            String href = link.attr("href");
            if (href.endsWith(".pdf")) {
                pdfLinks.add(href);
            }
        }

        return pdfLinks;
    }

    // Function to get the most recent PDF link
    private String getMostRecentPdfLink(List<String> pdfLinks) {
        String mostRecentPdfLink = null;
        int mostRecentYear = 0;

        for (String pdfLink : pdfLinks) {
            int year = getYearFromPdfLink(pdfLink);
            if (year > mostRecentYear) {
                mostRecentYear = year;
                mostRecentPdfLink = pdfLink;
            }
        }

        return mostRecentPdfLink; // Return the most recent PDF link found
    }

    private int getYearFromPdfLink(String pdfLink) {
        // Extract the filename from the URL
        String fileName = pdfLink.substring(pdfLink.lastIndexOf('/') + 1);

        // Use a regex to find a four-digit year in the filename
        Pattern yearPattern = Pattern.compile("\\b(\\d{4})\\b");
        Matcher matcher = yearPattern.matcher(fileName);

        if (matcher.find()) {
            // Return the first four-digit year found
            return Integer.parseInt(matcher.group(1));
        }

        // Return a default value if no year is found
        return 0; // or throw an exception, or handle it as needed
    }

    private String downloadPdf(String pdfLink, String pdfDirectory) throws IOException {
        // Create a URL object from the PDF link
        URL url = new URL(pdfLink);

        // Extract the filename from the URL
        String fileName = url.getFile().substring(url.getFile().lastIndexOf('/') + 1);
        String pdfFilePath = pdfDirectory + "/" + fileName;

        // Create input and output streams for downloading the PDF
        try (java.io.BufferedInputStream in = new java.io.BufferedInputStream(url.openStream());
             java.io.FileOutputStream fos = new java.io.FileOutputStream(pdfFilePath);
             java.io.BufferedOutputStream bout = new java.io.BufferedOutputStream(fos)) {

            byte[] data = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(data, 0, 1024)) != -1) {
                bout.write(data, 0, bytesRead);
            }
        }

        return pdfFilePath; // Return the path of the downloaded PDF
    }

    private String extractTextFromPdf(String pdfFilePath) throws IOException {
        // Load the PDF document
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            // Create a PDFTextStripper to extract text
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document); // Return the extracted text
        }
    }

    private String processPdfText(String pdfText) {
        StringBuilder results = new StringBuilder();
        String regex = "(?<collegeName>.+?),\\s*(?<collegeId>\\d+),\\s*(?<rank>\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(pdfText);

        results.append("Colleges List:\n");
        results.append("-------------------------------------------------\n");
        results.append(String.format("%-30s %-15s %-10s%n", "College Name", "College ID", "Rank"));
        results.append("-------------------------------------------------\n");

        while (matcher.find()) {
            String collegeName = matcher.group("collegeName").trim();
            String collegeId = matcher.group("collegeId").trim();
            String rank = matcher.group("rank").trim();
            results.append(String.format("%-30s %-15s %-10s%n", collegeName, collegeId, rank));
        }

        return results.toString(); // Return the formatted results
    }
}