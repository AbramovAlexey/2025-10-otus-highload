INSERT INTO users (first_name, second_name, birth_date, biography, city)
SELECT
    (ARRAY['Ivan', 'Petr', 'Alexey', 'Dmitry', 'Sergey', 'Mikhail', 'Andrey', 'Artem'])[floor(random() * 8 + 1)],
    (ARRAY['Petrov', 'Ivanov', 'Sidorov', 'Smirnov', 'Kuznetsov', 'Popov', 'Vasiliev'])[floor(random() * 7 + 1)],
    '1970-01-01'::date + (random() * (interval '40 years')),
    'Biography of user number ' || i,
    (ARRAY['Moscow', 'Saint Petersburg', 'Novosibirsk', 'Ekaterinburg', 'Kazan', 'Omsk'])[floor(random() * 6 + 1)]
FROM generate_series(1, 10000) AS i;
