-- Function: public.query5()

-- DROP FUNCTION public.query5();

CREATE OR REPLACE FUNCTION public.query5()
  RETURNS void AS
$BODY$DECLARE
	rec_row RECORD;
	rec_cursor CURSOR 
	FOR
	(select L1.OID as oid1,L2.OID as oid2,L3.OID as oid3
from "lod8" L1, "lod8" L2, "lod8" L3
where
L1.obj = '<http://knoesis.wright.edu/ssw/ont/weather.owl#WindSpeedObservation>'
and L1.pred = '<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>'
and L2.sub = L1.sub
and L2.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#samplingTime>'
and L3.sub = L2.obj 
and L3.pred = '<http://www.w3.org/2006/time#inXSDDateTime>'
and L3.obj like '"2004-08-10%');

BEGIN
	OPEN rec_cursor;
	LOOP
		FETCH rec_cursor INTO rec_row;
		EXIT WHEN NOT FOUND;
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid1,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid2,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid3,clock_timestamp());
		
	END LOOP;
CLOSE rec_cursor;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.query5()
  OWNER TO postgres;
