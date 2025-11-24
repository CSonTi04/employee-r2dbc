package employees;

public record Employee(String name, int yearOfBirth) {

    public int getAgeAt(int year) {
        if (year < yearOfBirth) {
            throw new IllegalArgumentException("Year %d cannot be before birth year %d".formatted(year, yearOfBirth));
        }
        return year - yearOfBirth;
    }
}
