package DataBase;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

// Класс, отображающий состояние сайта, и представляющий собой замену базе данных.
// Поскольку в ТЗ указаны хэш-таблицы для хранения состояния сайта, то
// Spring Boot не используется
public class Websites {
    //// Константы для генерации URL и HTML
    // Ссылка нашего сайта(-ов) (условно)
    private final String BEGIN_URL = "https://dns-name/";
    // Возможные символы в URL (условно)
    private final String SYMBOLS_URL = "abcdefghijklmnopqrstuvwxyz/-";
    // Возможные символы в HTML (условно)
    private final String SYMBOLS_HTML = "abcdefghijklmnopqrstuvwxyz<>!-/";
    // Минималььное количество символов в URL (условно)
    private final int MIN_SIZE_URL = 7;
    // Максимальное количество символов в URL (условно)
    private final int MAX_SIZE_URL = 15;
    // Минималььное количество символов в HTML (условно)
    private final int MIN_SIZE_HTML = 150;
    // Максимальное количество символов в URL (условно)
    private final int MAX_SIZE_HTML = 10000;

    // Имя и фамилия секретаря
    private static final String NAME_SURNAME = "Евгения Тодоренко";
    // Формат сообщения об изменениях на сайте
    public static final String MESSAGE_FORMAT = "Здравствуйте, дорогая " + NAME_SURNAME + '\n' +
            "\n" +
            "За последние сутки во вверенных Вам сайтах произошли следующие изменения:\n" +
            "\n" +
            "Исчезли следующие страницы: {\n%s}\n" +
            "Появились следующие новые страницы {\n%s}\n" +
            "Изменились следующие страницы {\n%s}\n" +
            "\n" +
            "С уважением,\n" +
            "автоматизированная система\n" +
            "мониторинга.";

    // Состояние сайта на вчера
    private volatile Map<String, String> oldInfo;
    // Состояние сайта на сегодня
    private volatile Map<String, String> newInfo;
    // Поле, являющееся буфером (ссылкой) для хранения ключа и значения
    private Map.Entry<String, String> node;

    // Вложенный класс, позволяющий работать с данными хэш-мапы (для возврата значений)
    public static class Pair {
        public static <K, V> Map.Entry<K, V> of(K first, V second) {
            return new AbstractMap.SimpleEntry<>(first, second);
        }
    }

    // Буфер для экономии памяти (не потокобезопасный)
    private StringBuilder temp;

    // Конструктор
    public Websites() {
        newInfo = new HashMap<>();
        temp = new StringBuilder(MAX_SIZE_HTML);
    }

    // Изменить данные на определённой странице сайта по ключу
    public synchronized void changeData(String pageURL) {
        newInfo.put(pageURL, generateRandomHTMLString());
    }

    // Изменить данные на рандомной странице сайта
    public synchronized void changeDataRandom(int count) {
        if(newInfo.size() >= count) {
            Map<Integer, Integer> tempy = new HashMap<>();
            while (count > 0) {
                int i = 0;
                int randomElement;

                // Цикл, который сгенерирует случайную позицию в хеш-мапе, которой не было
                // Алгоритм удлиняется по времени, но для демонстрации работы программы я считаю, что это не важно
                while(true) {
                    randomElement = randomValue(i, newInfo.size());
                    if(!tempy.containsKey(randomElement)) {
                        tempy.put(randomElement, randomElement);
                        break;
                    }
                }

                for(Map.Entry<String, String> info : newInfo.entrySet()) {
                    if(randomElement == i) {
                        info.setValue(generateRandomHTMLString());

                        break;
                    }

                    i++;
                }

                count--;
            }
        }
    }

    // Удалить из сайта определённую страницу по ключу
    public synchronized void removeSomePage(String pageURL) {
        newInfo.remove(pageURL);
    }

    // Удалить из сайта страницу (рандомно)
    public synchronized void removeSomePageRandom() {
        if(newInfo.size() > 0) {
            int i = 0;
            int randomElement = randomValue(i, newInfo.size());
            String keyForRemove = "";

            for(Map.Entry<String, String> info : newInfo.entrySet()) {
                if(randomElement == i) {
                    keyForRemove = info.getKey();

                    break;
                }

                i++;
            }

            if(!keyForRemove.equals("")) {
                newInfo.remove(keyForRemove);
            } else {
                System.out.println("Error remove element from newInfo collection! | not found key to remove");
            }
        }
    }

    // Добавить на сайт новую страницу
    public synchronized void putSomePage() {
        node = generateNode();
        newInfo.put(node.getKey(), node.getValue());
    }

    // Метод, вызывающий генерацию случайных ключа и значения
    private Map.Entry<String, String> generateNode() {
        return Pair.of(generateRandomURLString(), generateRandomHTMLString());
    }

    // Генерация случайной строки с символами из URL
    private String generateRandomURLString() {
        while(true) {
            // Очистка StringBuilder
            try {
                temp.delete(0, temp.length());
            } catch (RuntimeException e) {
                System.out.println("Out of bounds from StringBuilder | .delete(begin index, end index)");
                return BEGIN_URL + "zero/";
            }

            // Генерация случайной строки из символов URL с помощью стримов
            temp.append(new Random().ints(randomValue(MIN_SIZE_URL, MAX_SIZE_URL),
                         0,
                                          SYMBOLS_URL.length())
                                    .mapToObj(SYMBOLS_URL::charAt)
                                    .map(Object::toString)
                                    .collect(Collectors.joining()));

            // Вставка значения по-умолчанию в начало сгенерированной строки
            temp.insert(0, BEGIN_URL);

            // Если в хэш-мапе нет такого ключа, то возвращаем сгенерированную строку,
            // чтобы гарантировать уникальность сгенерированного URL
            if(!newInfo.containsKey(temp.toString())) {
                break;
            }
        }

        return temp.toString();
    }

    // Генерация случайной строки с символами из HTML
    private String generateRandomHTMLString() {
        while(true) {
            // Очистка StringBuilder
            try {
                temp.delete(0, temp.length());
            } catch (RuntimeException e) {
                System.out.println("Out of bounds from StringBuilder | .delete(begin index, end index)");
                return "<!DOCTYPE html>\n" +
                        "<html lang=\"ru\">\n" +
                        "<head>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "</body>\n" +
                        "</html>";
            }

            // Генерация случайной строки из символов URL с помощью стримов
            temp.append(new Random().ints(randomValue(MIN_SIZE_HTML, MAX_SIZE_HTML),
                         0,
                                          SYMBOLS_HTML.length())
                                    .mapToObj(SYMBOLS_HTML::charAt)
                                    .map(Object::toString)
                                    .collect(Collectors.joining()));

            // Если в хэш-мапе нет такого значения, то возвращаем сгенерированную строку,
            // чтобы гарантировать уникальность сгенерированного HTML
            if(!newInfo.containsValue(temp.toString())) {
                break;
            }
        }

        return temp.toString();
    }

    // Возврат случайного числа в диапазоне от min до max
    private int randomValue(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    // Метод, который "запоминает" текущее состояние сайта
    public void loggingWebsite() {
        oldInfo = new HashMap<>(newInfo);
    }

    // Метод, возвращающий строку, содержащую URL сайтов, которые появились
    public synchronized String findAppearedPages() {
        return newInfo.keySet().stream()
                                .filter(info -> !oldInfo.containsKey(info))
                                .map(info -> {
                                    return info + '\n';
                                })
                                .collect(Collectors.joining());
    }

    // Метод, возвращающий строку, содержащую URL сайтов, которые исчезли
    public synchronized String findDisappearedPages() {
        return oldInfo.keySet().stream()
                                .filter(info -> !newInfo.containsKey(info))
                                .map(info -> {
                                    return info + '\n';
                                })
                                .collect(Collectors.joining());
    }

    // Метод, возвращающий строку, содержащую URL сайтов, которые изменились
    public synchronized String findChangedPages() {
        return newInfo.entrySet().stream()
                .filter(info -> oldInfo.containsKey(info.getKey()) && !oldInfo.get(info.getKey()).equals(info.getValue()))
                .map(info -> {
                    return info.getKey() + '\n';
                }).collect(Collectors.joining());
    }

    @Override
    public String toString() {
        return "Old Site Info (" + oldInfo.size() + "):\n" +
                oldInfo.entrySet().stream().map(f -> {
                    return f.getKey() + " | " + f.getValue() + "\n";
                }).collect(Collectors.joining()) +

                "\n\nNew Site Info (" + newInfo.size() + "):\n" +
                newInfo.entrySet().stream().map(f -> {
                    return f.getKey() + " | " + f.getValue() + "\n";
                }).collect(Collectors.joining())

                + "\n";
    }
}
