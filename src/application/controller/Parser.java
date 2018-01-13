package application.controller;

import application.controller.exceptions.WrongInputException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Culring on 2018-01-13.
 */
public class Parser {
    public static Map<String, Map<String, Object>> parse(List<String> options) throws WrongInputException {
        Map<String, Map<String, Object>> parsedOptions = new HashMap<>();
        boolean isBaseOptionDefined = false,
                isAlgorithmChosen = false;

        while(options.size() > 0) {
            if(options.get(0).equals("--generate")) {
                if (isBaseOptionDefined) {
                    throw new WrongInputException();
                }
                isBaseOptionDefined = true;
                Map<String, Object> generateOptions = new HashMap<>();
                if(options.size() < 2){
                    throw new WrongInputException();
                }
                int size;
                try{
                    size = Integer.valueOf(options.get(1));
                }
                catch(NumberFormatException e){
                    throw new WrongInputException();
                }
                if(size <= 0){
                    throw new WrongInputException();
                }
                generateOptions.put("size", size);
                parsedOptions.put("--generate", generateOptions);
                options = options.subList(2, options.size());
            }
            else if(options.get(0).equals("--file")) {
                if (isBaseOptionDefined) {
                    throw new WrongInputException();
                }
                isBaseOptionDefined = true;
                Map<String, Object> fileOptions = new HashMap<>();
                fileOptions.put("filename", options.get(1));
                parsedOptions.put("--file", fileOptions);
                options = options.subList(2, options.size());
            }
            else if(options.get(0).equals("--help")){
                if (isBaseOptionDefined) {
                    throw new WrongInputException();
                }
                isBaseOptionDefined = true;
                parsedOptions.put("--help", null);
                options = options.subList(1, options.size());
            }
            else if(options.get(0).equals("--bfs")){
                if(isAlgorithmChosen){
                    throw new WrongInputException();
                }
                isAlgorithmChosen = true;
                parsedOptions.put("--bfs", null);
                options = options.subList(1, options.size());
            }
            else if(options.get(0).equals("--find_and_union")){
                if(isAlgorithmChosen){
                    throw new WrongInputException();
                }
                isAlgorithmChosen = true;
                parsedOptions.put("--find_and_union", null);
                options = options.subList(1, options.size());
            }
            else {
                throw new WrongInputException();
            }
        }
        return parsedOptions;
    }
}