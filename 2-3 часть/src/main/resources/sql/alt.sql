-- Удаляем старое поле, если оно существует
ALTER TABLE performance DROP COLUMN IF EXISTS performance_date;

-- Убедимся, что поле duration_minutes существует и имеет правильный тип
ALTER TABLE performance
    RENAME COLUMN duration TO duration_minutes;

ALTER TABLE performance
    ALTER COLUMN duration_minutes TYPE INTEGER USING (duration_minutes::integer);