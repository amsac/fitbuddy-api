-- Run once against the affected database while application writes are paused.
-- Preserves existing users; aligns the generated ID with the largest stored ID.
BEGIN;
LOCK TABLE public.users IN ACCESS EXCLUSIVE MODE;
SELECT setval(
    pg_get_serial_sequence('public.users', 'id'),
    COALESCE((SELECT MAX(id) FROM public.users), 1),
    EXISTS (SELECT 1 FROM public.users)
);
COMMIT;
