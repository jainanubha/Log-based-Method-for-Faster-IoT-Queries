-- Function: public.query13()

-- DROP FUNCTION public.query13();

CREATE OR REPLACE FUNCTION public.query13()
  RETURNS void AS
$BODY$DECLARE
	rec_row RECORD;
	rec_cursor CURSOR 
	FOR
	(select L1.OID as oid1 from "lod8" L1
where L1.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#parameter>'
and L1.obj = '<http://knoesis.wright.edu/ssw/ont/weather.owl#_WindSpeed>');

BEGIN
	OPEN rec_cursor;
	LOOP
		FETCH rec_cursor INTO rec_row;
		EXIT WHEN NOT FOUND;
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid1,clock_timestamp());
	END LOOP;
CLOSE rec_cursor;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.query13()
  OWNER TO postgres;
