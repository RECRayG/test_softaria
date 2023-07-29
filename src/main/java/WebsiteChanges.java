import DataBase.Websites;

// Программа реализована с помощью чистой Java Core в 1 потоке.
// Я бы эту задачу решал с использованием Spring Boot Mail (для отправки на почту)
// и ScheduledExecutorService для отправки сгенерированного письма на почту каждый день в установленное время.
// Поскольку в ТЗ даны хэш-таблицы для хранения состояния сайтов, то база данных, соответственно, не используется.
// Необходимо настроить консоль ОС под кириллицу.
public class WebsiteChanges {
    // Количество добавляемых сайтов
    private static final int COUNT_WEBSITES = 10;
    // Количество удаляемых сайтов
    public static final int COUNT_WEBSITES_REMOVE = 4;
    // Количество новых добавляемых сайтов
    public static final int COUNT_WEBSITES_ADD = 5;
    // Количество изменённых сайтов
    public static final int COUNT_WEBSITES_CHANGED = 3;

    public static void main(String[] args) {
        Websites test = new Websites();

        // Заполнить n сайтами текущую хэш-мапу
        for(int i = 0; i < COUNT_WEBSITES; i++) {
            test.putSomePage();
        }
        // Запомнить состояние текущих сайтов
        test.loggingWebsite();

        // Вывести в консоль начальное состояние сайтов (все 2 хеш-мапы)
        System.out.println(test);

        // Удалить n-ое количество сайтов
        for(int i = 0; i < COUNT_WEBSITES_REMOVE; i++) {
            test.removeSomePageRandom();
        }

        // Добавить n-ое количество сайтов
        for(int i = 0; i < COUNT_WEBSITES_ADD; i++) {
            test.putSomePage();
        }

        // Изменить n-ое количество сайтов
        // ВАЖНО (новые сайты не попадут в список изменённых
        // без сохранения состояния сайтов до вызова функции изменения).
        // Если фактическое кол-во изменённых сайтов меньше, чем COUNT_WEBSITES_CHANGED,
        // то недостающие сайты являются новыми.
        test.changeDataRandom(COUNT_WEBSITES_CHANGED);

        // Вывести в консоль конечное состояние сайтов (все 2 хеш-мапы)
        System.out.println(test);

        // Сообщение, которое является конечным требованием ТЗ
        System.out.printf(Websites.MESSAGE_FORMAT,
                test.findDisappearedPages(),
                test.findAppearedPages(),
                test.findChangedPages());
    }
}
