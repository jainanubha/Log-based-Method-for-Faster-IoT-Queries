-- Function: public.query4()

-- DROP FUNCTION public.query4();

CREATE OR REPLACE FUNCTION public.query4()
  RETURNS void AS
$BODY$DECLARE
	rec_row RECORD;
	rec_cursor CURSOR 
	FOR
	(select T1.OID as oid1,T2.OID as oid2,T3.OID as oid3,T4.OID as oid4,T5.OID as oid5
from "lod8" T1, "lod8" T2, "lod8" T3, "lod8" T4, "lod8" T5
where
T1.obj = '<http://knoesis.wright.edu/ssw/ont/weather.owl#RainfallObservation>'
and T1.pred = '<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>'
and T2.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#result>'
and T2.sub = T1.sub
and T3.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#uom>'
and T3.obj = '<http://knoesis.wright.edu/ssw/ont/weather.owl#centimeters>'
and T3.sub = T2.obj
and T4.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#floatValue>'
and T4.sub = T3.sub
and T5.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#generatedObservation>'
and T1.sub = T5.obj
and T5.sub IN 
(select L2.sub from "lod8" L2, "lod8" L3, "lod8" L4, "lod8" L5, "lod8" L6
where
L2.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#generatedObservation>'
and L3.sub = L2.obj
and L3.pred = '<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>'
and L3.obj = '<http://knoesis.wright.edu/ssw/ont/weather.owl#RelativeHumidityObservation>'
and L4.sub = L3.sub
and L4.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#result>'
and L5.obj = '<http://knoesis.wright.edu/ssw/ont/weather.owl#percent>'
and L5.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#uom>'
and L5.sub = L4.obj
and L6.sub = L5.sub
and L6.pred = '<http://knoesis.wright.edu/ssw/ont/sensor-observation.owl#floatValue>'
and L6.obj < '"23.0"^^<http://www.w3.org/2001/XMLSchema#float>"'));

BEGIN
	OPEN rec_cursor;
	LOOP
		FETCH rec_cursor INTO rec_row;
		EXIT WHEN NOT FOUND;
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid1,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid2,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid3,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid4,clock_timestamp());
		INSERT INTO LogTable(OID,start_time) values(rec_row.oid5,clock_timestamp());
		
	END LOOP;
CLOSE rec_cursor;
END;$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION public.query4()
  OWNER TO postgres;
