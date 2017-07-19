-- Function: public.query12()

-- DROP FUNCTION public.query12();

CREATE OR REPLACE FUNCTION public.query12()
  RETURNS void AS
$BODY$DECLARE
	rec_row RECORD;
	rec_cursor CURSOR 
	FOR
	(select L1.OID as oid1, L2.OID as oid2 from "lod8" L1, "lod8" L2
where L1.obj = '<http://knoesis.wright.edu/ssw/LocatedNearRelODT12>'
and L1.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#hasLocatedNearRel>'
and L2.sub = L1.sub
and L2.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#ID>');

BEGIN
	OPEN rec_cursor;
	LOOP
		FETCH rec_cursor INTO rec_row;
		EXIT WHEN NOT FOUND;
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid1,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid2,clock_timestamp());
	END LOOP;
CLOSE rec_cursor;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.query12()
  OWNER TO postgres;
