class IDGenerator {
    private static int id = 0;

    public static int getNextId() {
        return id++;
    }
}
