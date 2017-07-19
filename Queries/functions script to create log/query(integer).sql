-- Function: public.query(integer)

-- DROP FUNCTION public.query(integer);

CREATE OR REPLACE FUNCTION public.query(query_num integer)
  RETURNS void AS
$BODY$DECLARE
BEGIN	
CASE query_num
WHEN 1 THEN
    PERFORM query1();
WHEN 2 THEN
    PERFORM query2();
WHEN 3 THEN
    PERFORM query3();
WHEN 4 THEN
    PERFORM query4();
WHEN 5 THEN
    PERFORM query5();
WHEN 6 THEN
    PERFORM query6();
WHEN 7 THEN
    PERFORM query7();
WHEN 8 THEN
    PERFORM query8();
WHEN 9 THEN
    PERFORM query9();
WHEN 10 THEN
    PERFORM query10();
WHEN 11 THEN
    PERFORM query11();
WHEN 12 THEN
    PERFORM query12();
WHEN 13 THEN
    PERFORM query13();
WHEN 14 THEN
    PERFORM query14();
WHEN 15 THEN
    PERFORM query15();
WHEN 16 THEN
    PERFORM query16();
END CASE;

END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.query(integer)
  OWNER TO postgres;
