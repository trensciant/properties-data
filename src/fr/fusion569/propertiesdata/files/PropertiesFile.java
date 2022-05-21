package fr.fusion569.propertiesdata.files;

import fr.fusion569.propertiesdata.PropertiesData;
import fr.fusion569.propertiesdata.events.booleans.BooleanEvent;
import fr.fusion569.propertiesdata.events.Event;
import fr.fusion569.propertiesdata.events.numbers.DoubleNumberEvent;
import fr.fusion569.propertiesdata.events.numbers.FloatNumberEvent;
import fr.fusion569.propertiesdata.events.numbers.IntegerNumberEvent;
import fr.fusion569.propertiesdata.events.strings.StringEvent;
import fr.fusion569.propertiesdata.utils.KeyValueSeparator;
import fr.fusion569.propertiesdata.utils.StandardDirectoryCreationType;
import fr.fusion569.propertiesdata.utils.StandardFileCreationType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PropertiesFile {

    private final String path, name, absolutePath;
    private final StandardFileCreationType standardFileCreationType;
    private final StandardDirectoryCreationType standardDirectoryCreationType;
    private final KeyValueSeparator keyValueSeparator;
    private final File file;
    private BufferedReader bufferedReader;

    /**
     * The {@link File} extension for a custom file properties.
     */
    public static final String FILE_EXTENSION = ".proper";

    public PropertiesFile(String path, String name, StandardFileCreationType standardFileCreationType, StandardDirectoryCreationType standardDirectoryCreationType, KeyValueSeparator keyValueSeparator) {
        this.path = path;
        this.name = name + FILE_EXTENSION;
        this.absolutePath = this.path + this.name;
        this.standardFileCreationType = standardFileCreationType;
        this.standardDirectoryCreationType = standardDirectoryCreationType;
        this.keyValueSeparator = keyValueSeparator;
        this.file = new File(this.path, this.name);
    }

    /**
     * Get the {@link String} {@link File} path.
     *
     * @return
     * The {@link String} {@link File} path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Get the {@link String} {@link File} name.
     *
     * @return
     * The {@link String} {@link File} name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the {@link String} {@link File} absolute path.
     *
     * @return
     * The {@link String} {@link File} absolute path.
     */
    public String getAbsolutePath() {
        return absolutePath;
    }

    /**
     * Get the {@link File}.
     *
     * @return
     * The {@link File}.
     */
    public StandardFileCreationType getStandardFileCreationType() {
        return standardFileCreationType;
    }

    /**
     * Get the {@link StandardDirectoryCreationType}.
     *
     * @return
     * The {@link StandardDirectoryCreationType}.
     */
    public StandardDirectoryCreationType getStandardDirectoryCreationType() {
        return standardDirectoryCreationType;
    }

    /**
     * Get the {@link KeyValueSeparator}.
     *
     * @return
     * The {@link KeyValueSeparator}.
     */
    public KeyValueSeparator getKeyValueSeparator() {
        return keyValueSeparator;
    }

    /**
     * Get the {@link File}.
     *
     * @return
     * The {@link File}.
     */
    public File getFile() {
        return file;
    }

    /**
     * Get the {@link BufferedReader}.
     *
     * @return
     * The {@link BufferedReader}.
     */
    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    /**
     * Create the {@link File} as constructor and its copy if the {@link StandardFileCreationType} as constructor is {@link StandardFileCreationType#FILE_WANTED_WITH_COPY}.
     */
    public void create() {
        final File dir = new File(this.path);

        if(!dir.exists()) {
            if(this.standardDirectoryCreationType.equals(StandardDirectoryCreationType.CREATE)) {
                dir.mkdir();
                System.out.println(PropertiesData.getLogsPrefix() + "The directory " + this.absolutePath + " has been created.");
            }
        } else {
            System.out.println(PropertiesData.getLogsPrefix() + "No directory created.");
        }
        if(!this.file.exists()) {
            try {
                this.file.createNewFile();
                System.out.println(PropertiesData.getLogsPrefix() + "The file " + this.absolutePath + " has been created.");
            } catch(IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(PropertiesData.getLogsPrefix() + "No file created.");
        }
        if(this.standardFileCreationType.equals(StandardFileCreationType.FILE_WANTED_WITH_COPY)) {
            this.createCopy();
        }
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file), StandardCharsets.UTF_8));
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a {@link File} copy.
     */
    private void createCopy() {
        final File copy = new File(this.path, "copy-" + this.name);

        if(!copy.exists()) {
            try {
                copy.createNewFile();
                System.out.println(PropertiesData.getLogsPrefix() + "The copy file " + this.path + "copy-" + this.name + " of " + this.absolutePath + " has been created.");
            } catch(IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(PropertiesData.getLogsPrefix() + "No copy created.");
        }
    }

    /**
     * Close and make a new instance of the {@link PropertiesFile} {@link BufferedReader}.
     */
    private void resetBufferedReader() {
        try {
            this.bufferedReader.close();
            this.bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file), StandardCharsets.UTF_8));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Throw the exceptions {@link IllegalArgumentException} if the key is "" or starts with " ".
     *
     * @param key
     * The key to declare exceptions if needed.
     */
    private void throwKeyExceptions(String key) {
        if(key.equals(""))
            throw new IllegalArgumentException("You must set a valid String key");
        if(key.startsWith(" "))
            throw new IllegalArgumentException("Your String key can't starts with ' '.");
    }

    /**
     * Throw an {@link IllegalArgumentException} if the {@link PropertiesData} {@link Event} {@link java.util.List} is empty.
     */
    private void throwEmptyEventsListException() {
        if(PropertiesData.getEventsList().isEmpty()) {
            throw new IllegalArgumentException("You must register an event before using one.");
        }
    }

    /**
     * Get a {@link String} value from a {@link String} key without quotation marks.
     *
     * @param key
     * The {@link String} key to get a {@link String} value without quotation marks.
     *
     * @return
     * A {@link String} value from a {@link String} key without quotation marks.
     */
    private String getStringWithoutQuotationMarks(String key) {
        this.throwKeyExceptions(key);
        String line;

        try {
            line = this.bufferedReader.readLine();

            while(line != null) {
                if(line.startsWith(key)){
                    if(line.contains(this.keyValueSeparator.getSeparator())) {
                        final String[] kv = line.split(this.keyValueSeparator.getSeparator());

                        if(kv.length >= 3) {
                            for(int i = 2; i < kv.length; i++) {
                                kv[1] = kv[1] + this.keyValueSeparator.getSeparator() + kv[i];
                            }
                        }
                        this.resetBufferedReader();
                        return kv[1];
                    }
                }
                line = this.bufferedReader.readLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        this.resetBufferedReader();
        throw new IllegalArgumentException(PropertiesData.getLogsPrefix() + "Invalid key: '" + key + "' or key value separator '" + this.keyValueSeparator.getSeparator() + "'.");
    }

    /**
     * Get a {@link String} value from a {@link String} key with quotation marks.
     *
     * @param key
     * The {@link String} key to get a {@link String} value with quotation marks.
     *
     * @return
     * A {@link String} value from a {@link String} key with quotation marks.
     */
    public String getString(String key) {
        this.throwKeyExceptions(key);
        String line;

        try {
            line = this.bufferedReader.readLine();

            while(line != null) {
                if(line.startsWith(key)){
                    if(line.contains(this.keyValueSeparator.getSeparator())) {
                        final String[] kv = line.split(this.keyValueSeparator.getSeparator());

                        if(kv.length >= 3) {
                            for(int i = 2; i < kv.length; i++) {
                                kv[1] = kv[1] + this.keyValueSeparator.getSeparator() + kv[i];
                            }
                        }
                        this.resetBufferedReader();
                        if(kv[1].startsWith("\"") && kv[1].endsWith("\"")) {
                            return kv[1].substring(1, kv[1].length() - 1);
                        } else {
                            throw new IllegalArgumentException(PropertiesData.getLogsPrefix() + "Your value must include \" at end and the beginning.");
                        }
                    }
                }
                line = this.bufferedReader.readLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        this.resetBufferedReader();
        throw new IllegalArgumentException(PropertiesData.getLogsPrefix() + "Invalid key: '" + key + "' or key value separator '" + this.keyValueSeparator.getSeparator() + "'.");
    }

    /**
     * Get all {@link StringEvent} registered in {@link PropertiesData#getEventsList()} to execute them.
     * Also put the {@link String} parsed value in the {@link StringEvent#on(String, String)} method.
     *
     * @param key
     * The {@link String} key to put it in the {@link IntegerNumberEvent#on(String, int)} method.
     */
    public void getStringWithListener(String key) {
        this.throwEmptyEventsListException();
        final String val = this.getString(key);

        for(Event event : PropertiesData.getEventsList()) {
            if(event instanceof StringEvent) {
                ((StringEvent) event).on(key, val);
            }
        }
    }

    /**
     * Get a {@link Integer} value from a {@link String} key without listener check.
     *
     * @param key
     * The {@link String} key to get a {@link Integer} value without listener check.
     *
     * @return
     * A {@link Integer} value from a {@link String} key without listener check.
     */
    public int getInteger(String key) {
        return Integer.parseInt(this.getStringWithoutQuotationMarks(key));
    }

    /**
     * Get all {@link IntegerNumberEvent} registered in {@link PropertiesData#getEventsList()} to execute them.
     * Also put the {@link Integer} parsed value in the {@link IntegerNumberEvent#on(String, int)} method.
     *
     * @param key
     * The {@link String} key to put it in the {@link IntegerNumberEvent#on(String, int)} method.
     */
    public void getIntegerWithListener(String key) {
        this.throwEmptyEventsListException();
        final int val = Integer.parseInt(this.getStringWithoutQuotationMarks(key));

        for(Event event : PropertiesData.getEventsList()) {
            if(event instanceof IntegerNumberEvent) {
                ((IntegerNumberEvent) event).on(key, val);
            }
        }
    }

    /**
     * Get a {@link Double} value from a {@link String} key.
     *
     * @param key
     * The {@link String} key to get a {@link Double} value.
     *
     * @return
     * A {@link Double} value from a {@link String} key.
     */
    public double getDouble(String key) {
        return Double.parseDouble(this.getStringWithoutQuotationMarks(key));
    }

    /**
     * Get all {@link DoubleNumberEvent} registered in {@link PropertiesData#getEventsList()} to execute them.
     * Also put the {@link Double} parsed value in the {@link DoubleNumberEvent#on(String, double)} method.
     *
     * @param key
     * The {@link String} key to put it in the {@link DoubleNumberEvent#on(String, double)} method.
     */
    public void getDoubleWithListener(String key) {
        this.throwEmptyEventsListException();
        final double val = Double.parseDouble(this.getStringWithoutQuotationMarks(key));

        for(Event event : PropertiesData.getEventsList()) {
            if(event instanceof DoubleNumberEvent) {
                ((DoubleNumberEvent) event).on(key, val);
            }
        }
    }

    /**
     * Get a {@link Float} value from a {@link String} key.
     *
     * @param key
     * The {@link String} key to get a {@link Float} value.
     *
     * @return
     * A {@link Float} value from a {@link String} key.
     */
    public float getFloat(String key) {
        return Float.parseFloat(this.getStringWithoutQuotationMarks(key));
    }

    /**
     * Get all {@link FloatNumberEvent} registered in {@link PropertiesData#getEventsList()} to execute them.
     * Also put the {@link Float} parsed value in the {@link FloatNumberEvent#on(String, float)} method.
     *
     * @param key
     * The {@link String} key to put it in the {@link FloatNumberEvent#on(String, float)} method.
     */
    public void getFloatWithListener(String key) {
        this.throwEmptyEventsListException();
        final float val = Float.parseFloat(this.getStringWithoutQuotationMarks(key));

        for(Event event : PropertiesData.getEventsList()) {
            if(event instanceof FloatNumberEvent) {
                ((FloatNumberEvent) event).on(key, val);
            }
        }
    }

    /**
     * Get a {@link Boolean} value from a {@link String} key.
     *
     * @param key
     * The {@link String} key to get a {@link Boolean} value.
     *
     * @return
     * A {@link Boolean} value from a {@link String} key.
     */
    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(this.getStringWithoutQuotationMarks(key));
    }

    /**
     * Get all {@link BooleanEvent} registered in {@link PropertiesData#getEventsList()} to execute them.
     * Also put the {@link Boolean} parsed value in the {@link BooleanEvent#on(String, boolean)} method.
     *
     * @param key
     * The {@link String} key to put it in the {@link BooleanEvent#on(String, boolean)} method.
     */
    public void getBooleanWithListener(String key) {
        this.throwEmptyEventsListException();
        final boolean val = Boolean.parseBoolean(this.getStringWithoutQuotationMarks(key));

        for(Event event : PropertiesData.getEventsList()) {
            if(event instanceof BooleanEvent) {
                ((BooleanEvent) event).on(key, val);
            }
        }
    }

    /*
    public boolean isEmpty() {
        final List<String> lines = new ArrayList<>();
        String line;

        try {
            line = this.bufferedReader.readLine();

            while(line != null) {
                lines.add(line);
                line = this.bufferedReader.readLine();
            }
            for(String s : lines) {
                if(!s.equals("")) {
                    this.resetBufferedReader();
                    return false;
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        this.resetBufferedReader();
        return true;
    }
     */
}
