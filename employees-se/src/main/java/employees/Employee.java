package employees;

import java.util.ArrayList;
import java.util.List;

public record Employee(String name, int yearOfBirth) {

    public int getAgeAt(int year) {
        if (year < yearOfBirth) {
            throw new IllegalArgumentException("Year %d cannot be before birth year %d".formatted(year, yearOfBirth));
        }
        return year - yearOfBirth;
    }

    public List<Integer> getFirstYears() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            result.add(yearOfBirth + i);
        }
        return result;
    }
}
