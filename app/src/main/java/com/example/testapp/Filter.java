package com.example.testapp;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Filter {
        // Main filtering method with multiple criteria
        public static List<College> filterColleges(
                List<College> colleges,
                Integer minCutoff,
                Integer maxCutoff,
                String location,
                Double minRating
        ) {
            return colleges.stream()
                    .filter(college -> filterByCutoff(college, minCutoff, maxCutoff))
                    .filter(college -> filterByLocation(college, location))
                    .filter(college -> filterByRating(college, minRating))
                    .collect(Collectors.toList());
        }

        // Cutoff Range Filter
        private static boolean filterByCutoff(College college, Integer minCutoff, Integer maxCutoff) {
            // If no cutoff range specified, return true
            if (minCutoff == null && maxCutoff == null) return true;

            // If only min cutoff specified
            if (minCutoff != null && maxCutoff == null) {
                return college.getCutoff() >= minCutoff;
            }

            // If only max cutoff specified
            if (minCutoff == null && maxCutoff != null) {
                return college.getCutoff() <= maxCutoff;
            }

            // Both min and max cutoff specified
            return college.getCutoff() >= minCutoff && college.getCutoff() <= maxCutoff;
        }

        // Location Filter
        private static boolean filterByLocation(College college, String location) {
            // If no location specified, return true
            if (location == null || location.trim().isEmpty()) return true;

            // Case-insensitive location match
            return college.getLocation().trim()
                    .equalsIgnoreCase(location.trim());
        }

        // Rating Filter
        private static boolean filterByRating(College college, Double minRating) {
            // If no minimum rating specified, return true
            if (minRating == null) return true;

            return college.getRating() >= minRating;
        }

        // Advanced Filtering with Multiple Locations
        public static List<College> filterCollegesByMultipleLocations(
                List<College> colleges,
                List<String> locations
        ) {
            if (locations == null || locations.isEmpty()) return colleges;

            return colleges.stream()
                    .filter(college -> locations.stream()
                            .anyMatch(loc ->
                                    college.getLocation().trim()
                                            .equalsIgnoreCase(loc.trim())
                            ))
                    .collect(Collectors.toList());
        }

        // Course-based Filtering
        public static List<College> filterCollegesByCourses(
                List<College> colleges,
                List<String> courses
        ) {
            if (courses == null || courses.isEmpty()) return colleges;

            return colleges.stream()
                    .filter(college ->
                            college.getCourses().stream()
                                    .anyMatch(courseName ->
                                            courses.stream()
                                                    .anyMatch(filterCourse ->
                                                            courseName.trim()
                                                                    .toLowerCase()
                                                                    .contains(filterCourse.trim().toLowerCase())
                                                    )
                                    ))
                    .collect(Collectors.toList());
        }

        // Comprehensive Filter Builder
        public static class CollegeFilterBuilder {
            private List<College> colleges;
            private Integer minCutoff;
            private Integer maxCutoff;
            private String location;
            private Double minRating;
            private List<String> specificLocations;
            private List<String> courses;

            public CollegeFilterBuilder(List<College> colleges) {
                this.colleges = colleges;
            }

            public CollegeFilterBuilder withCutoffRange(Integer min, Integer max) {
                this.minCutoff = min;
                this.maxCutoff = max;
                return this;
            }

            public CollegeFilterBuilder withLocation(String location) {
                this.location = location;
                return this;
            }

            public CollegeFilterBuilder withMinRating(Double rating) {
                this.minRating = rating;
                return this;
            }

            public CollegeFilterBuilder withLocations(List<String> locations) {
                this.specificLocations = locations;
                return this;
            }

            public CollegeFilterBuilder withCourses(List<String> courses) {
                this.courses = courses;
                return this;
            }

            public List<College> filter() {
                List<College> filteredColleges = colleges;

                // Apply filters sequentially
                if (minCutoff != null || maxCutoff != null) {
                    filteredColleges = filterColleges(
                            filteredColleges,
                            minCutoff,
                            maxCutoff,
                            null,
                            null
                    );
                }

                if (location != null) {
                    filteredColleges = filterColleges(
                            filteredColleges,
                            null,
                            null,
                            location,
                            null
                    );
                }

                if (minRating != null) {
                    filteredColleges = filterColleges(
                            filteredColleges,
                            null,
                            null,
                            null,
                            minRating
                    );
                }

                if (specificLocations != null && !specificLocations.isEmpty()) {
                    filteredColleges = filterCollegesByMultipleLocations(
                            filteredColleges,
                            specificLocations
                    );
                }

                if (courses != null && !courses.isEmpty()) {
                    filteredColleges = filterCollegesByCourses(
                            filteredColleges,
                            courses
                    );
                }

                return filteredColleges;
            }
        }

        // Usage Example in Activity
        private void demonstrateFiltering(List<College> allColleges) {
            // Simple filtering
            List<College> filteredColleges = Filter.filterColleges(
                    allColleges,
                    1000,   // Min Cutoff
                    3000,   // Max Cutoff
                    "Delhi", // Location
                    8.0     // Minimum Rating
            );

            // Builder Pattern Filtering
            List<College> advancedFilteredColleges = new CollegeFilterBuilder(allColleges)
                    .withCutoffRange(1000, 3000)
                    .withLocation("Delhi")
                    .withMinRating(8.0)
                    .withCourses(Arrays.asList("Computer Science"))
                    .filter();
        }
}

