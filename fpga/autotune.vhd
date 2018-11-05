LIBRARY IEEE;
USE IEEE.std_logic_1164.ALL;
USE IEEE.numeric_std.ALL;


ENTITY autotune IS
	PORT(
	reset : IN STD_LOGIC;													--asynchronous reset
	SCLK : IN STD_LOGIC; 													--SPI master clock
	MOSI : IN STD_LOGIC; 													--SPI input; MasterOutput SlaveInput
	MISO : OUT STD_LOGIC; 													--SPI output; MasterInput SlaveOutput
	display1 : OUT STD_LOGIC_VECTOR(6 DOWNTO 0);										--6 7-segment displays used for debugging		
	display2 : OUT STD_LOGIC_VECTOR(6 DOWNTO 0);
	display3 : OUT STD_LOGIC_VECTOR(6 DOWNTO 0);
	display4 : OUT STD_LOGIC_VECTOR(6 DOWNTO 0);
	display5 : OUT STD_LOGIC_VECTOR(6 DOWNTO 0);
	display6 : OUT STD_LOGIC_VECTOR(6 DOWNTO 0);
	switch9 : IN STD_LOGIC;													--switches used for sensitivity in measurements of zero crossings
	switch8 : IN STD_LOGIC;
	switch1 : IN STD_LOGIC;
	switch0 : IN STD_LOGIC);
END ENTITY autotune;


ARCHITECTURE bhv OF autotune IS

FUNCTION hex2display (n:std_logic_vector(3 DOWNTO 0)) RETURN std_logic_vector IS						--function to display numbers on the displays, copied from DH Lab

VARIABLE res : std_logic_vector(6 DOWNTO 0);
  BEGIN
    CASE n IS          --        gfedcba; low active
	    WHEN "0000" => RETURN NOT "0111111";
	    WHEN "0001" => RETURN NOT "0000110";
	    WHEN "0010" => RETURN NOT "1011011";
	    WHEN "0011" => RETURN NOT "1001111";
	    WHEN "0100" => RETURN NOT "1100110";
	    WHEN "0101" => RETURN NOT "1101101";
	    WHEN "0110" => RETURN NOT "1111101";
	    WHEN "0111" => RETURN NOT "0000111";
	    WHEN "1000" => RETURN NOT "1111111";
	    WHEN "1001" => RETURN NOT "1101111";
	    WHEN "1010" => RETURN NOT "1110111";
	    WHEN "1011" => RETURN NOT "1111100";
	    WHEN "1100" => RETURN NOT "0111001";
	    WHEN "1101" => RETURN NOT "1011110";
	    WHEN "1110" => RETURN NOT "1111001";
	    WHEN OTHERS => RETURN NOT "1110001";			
    END CASE;
  END hex2display;

	
TYPE mem IS ARRAY(0 TO 4095) OF STD_LOGIC_VECTOR(15 DOWNTO 0); 									--creation of inferred RAM, information from
SIGNAL ram_block : mem; 													--https://www.intel.com/content/www/us/en/programmable/quartushelp/14.1/mergedProjects/hdl/vhdl/vhdl_pro_ram_inferred.htm

BEGIN

PROCESS(reset, SCLK, switch9, switch8, switch1, switch0)
	VARIABLE i : INTEGER := 0;												--counter of SPI clock
	VARIABLE c : INTEGER := 0;												--counter of bytes
	VARIABLE code : STD_LOGIC_VECTOR(15 DOWNTO 0) := "0000000000000000";							--used for reading data input from MOSI
	VARIABLE converted : STD_LOGIC_VECTOR(15 DOWNTO 0) := "0000000000000000";						--to rearrange the bytes from little to big endian
	VARIABLE timeframe : STD_LOGIC_VECTOR(15 DOWNTO 0) := "0000000000000000";						--information about the timeframe of the audio samples are stored in here
	VARIABLE nr_of_bytes : INTEGER range 6 to 8192;										--the number of bytes that will be sent in that packet
	CONSTANT marge : INTEGER := 512;											--level of trigger for zero crossings
	VARIABLE zerocrossings : INTEGER := 0;											--amount of zero crossings counted
	VARIABLE amplitudesign : STD_LOGIC;											--store whether the previous sample is positive or negative in sign
	VARIABLE std_zero : STD_LOGIC_VECTOR(15 DOWNTO 0) := "0000000000000000";						--vector form of zero crossings
	VARIABLE previous_zeros : INTEGER := 0;											--number of zero crossings of the previous packket is stored here
	VARIABLE zeros_original : INTEGER := 0;											--information about the zero crossings of the original vocals
	VARIABLE previous_zeros_original : INTEGER := 0;									--number of zero crossings of the original voice of the previous packet
	VARIABLE stdpzo : STD_LOGIC_VECTOR(15 DOWNTO 0) := "0000000000000000";							--vector of previous_zeros_original
	VARIABLE write_address : INTEGER := 0; 											--to write to inferred RAM
	VARIABLE read_address: INTEGER := 0;											--to read from the inferred RAM
	VARIABLE autotuned : STD_LOGIC_VECTOR(15 DOWNTO 0) := "0000000000000000";						--the samples from the previous packet will be read into this vector before the can be sent back
	VARIABLE marge_switch : INTEGER range 512 to 4096;									--marge multiplied by a value determined by switch 8 and 9 for variable sensitivity
	VARIABLE vectorswitch : STD_LOGIC_VECTOR(15 DOWNTO 0) := STD_LOGIC_VECTOR(TO_UNSIGNED(marge_switch/2, 16));		--vector form of marge_switch
	VARIABLE write_high : STD_LOGIC := '0';											--to distinguish between the two parts of the RAM; if we read to the lower part, data will be written to the upper part and vice versa
	VARIABLE WE : STD_LOGIC := '0';												--write enable, allows the program to write to the RAM when it is 1
	VARIABLE RE : STD_LOGIC := '0';												--read enable, allows the program to read from the RAM when it is set to 1
	VARIABLE Kees : INTEGER := 100;												--used to store the ratio between previous zeros and previous zeros original
	VARIABLE sensitivity : INTEGER;
	VARIABLE max_autotune : INTEGER;
	VARIABLE std_sens : STD_LOGIC_VECTOR(3 DOWNTO 0);

	BEGIN
		IF switch9 = '0' AND switch8 = '0' THEN marge_switch := marge;							--asynchronous marge select 
		ELSIF switch9 = '0' AND switch8 = '1' THEN marge_switch := 2*marge;						--encoding for the sensitivity multiplication:
		ELSIF switch9 = '1' AND switch8 = '0' THEN marge_switch := 4*marge;						--00 => 1 times marge, 01 => 2 times marge 
		ELSE marge_switch := 8*marge; END IF;										--10 => 4 times marge, 11 => 8 times marge
		display6 <= hex2display(vectorswitch(11 DOWNTO 8));								--display marge_switch on display 6
		IF switch1 = '0' AND switch0 = '0' THEN sensitivity := 2; max_autotune := 10; std_sens := "0010";		--asynchronous autotune sensitivity
		ELSIF switch1 = '0' AND switch0 = '1' THEN sensitivity := 3; max_autotune := 15; std_sens := "0011";		--same way of selecting the sensitivity as for marge
		ELSIF switch1 = '1' AND switch0 = '0' THEN sensitivity := 4; max_autotune := 20; std_sens := "0100";
		ELSE sensitivity := 8; max_autotune := 40; std_sens := "1000"; END IF;
		display5 <= hex2display(std_sens(3 DOWNTO 0));									--display sensitivity on display 5

		IF reset = '0' THEN												--reset values when key0 is pressed
			i := 0;
			c := 0;
			MISO <= '0';
			code := "0000000000000000";
	 		display1 <= hex2display("0000");
	 		display2 <= hex2display("0000");
	 		display3 <= hex2display("0000");
	 		display4 <= hex2display("0000");
	 		display5 <= hex2display("0000");
	 		display6 <= hex2display("0000");
			write_address := 0;
			read_address := 0;
			autotuned := "0000000000000000";
			previous_zeros := 0;
			previous_zeros_original := 0;
			Kees := 100;

		ELSIF SCLK'event AND SCLK = '1' THEN 										--rising edge of SPI clock
			code(15-i) := MOSI; 											--read MOSI into code
			converted := code(7 DOWNTO 0) & code(15 DOWNTO 8);							--make code readable by swapping from least significant byte to most significant byte
			i := i + 1;												--increase bit counter
			IF i = 16 THEN i := 0; c := c + 2; END IF; 								--reset i if 2 bytes have been read and increment c
			IF c >= nr_of_bytes THEN 										--when all the bytes have been sent:
				c := 0; 												--reset c
				RE := '0';												--make read enable low
				WE := '0';												--make write enable low
				IF Kees <= 100+max_autotune AND Kees >= 100-max_autotune AND previous_zeros > 0 AND previous_zeros_original > 0 THEN	--set the new value of Kees based on the old values of previos zeros and previous zeros original
					IF 100*previous_zeros_original/previous_zeros > (100+sensitivity)*Kees/100 THEN		--maximum change in Kees upwards
						Kees := (100+sensitivity)*Kees/100;
					ELSIF 100*previous_zeros_original/previous_zeros < (100-sensitivity)*Kees/100 THEN	--and maximum change in Kees down
						Kees := (100-sensitivity)*Kees/100;
					ELSE Kees := 100*previous_zeros_original/previous_zeros;				--if Kees changes withing that range, this new value will be assigned to Kees
					END IF;	
				ELSIF Kees < 100-max_autotune THEN Kees := 100-max_autotune;								--absolute minimum of Kees
				ELSIF Kees > 100+max_autotune THEN Kees := 100+max_autotune;								--absolute maximum of Kees
				END IF;
				previous_zeros_original := zeros_original;							--store the current value of zeros_original in previous_zeros_original which will be used for autotuning
				stdpzo := STD_LOGIC_VECTOR(TO_UNSIGNED(previous_zeros_original, 16));				--vector form of previous_zeros_original
				IF write_high = '0' THEN 									--select the other part of RAM
					write_high := '1';
				ELSE write_high := '0'; END IF;
			END IF; 
			IF c = 0 THEN 												--first 2 bytes
				MISO <= std_zero(15-i); 									--start sending nr. of zero crossings of previous packet
				previous_zeros := zerocrossings;								--save the amount of zero crossings fo the previous packet
				display1 <= hex2display(stdpzo(3 DOWNTO 0)); 							--also display zerocrossings for debugging
	 			display2 <= hex2display(stdpzo(7 DOWNTO 4));
	 			display3 <= hex2display(std_zero(3 DOWNTO 0));
	 			display4 <= hex2display(std_zero(7 DOWNTO 4));
			ELSIF c = 2 THEN 											--3rd and 4th bytes
				MISO <= timeframe(15-i); 									--send timeframe corresponding to the zero crossings that have been sent right before
				IF i = 0 THEN
					IF TO_INTEGER(UNSIGNED(converted(11 DOWNTO 0))) < 6 THEN nr_of_bytes := 6;		--in the case that the number of bytes is smaller than 6, it will be set to 6 because the header is always 6 bytes
					ELSE nr_of_bytes := TO_INTEGER(UNSIGNED(converted(11 DOWNTO 0))) + 1; END IF;		--first two bytes have been received, they contain the information of the number of bytes that will be sent
					zerocrossings := 0; 									--reset the amount of zero crossings
					std_zero := "0000000000000000";
				END IF;
			ELSIF c = 4 THEN											--5th and 6th byte
				IF i = 0 THEN 
					timeframe := converted; 								--the timeframe that was received during the 3rd and 4th byte has been received completely now and is stored in timeframe
					MISO <= '0'; 										--make output 0
				ELSIF i = 15 THEN
					RE := '1';										--switch read enable to 1 so the samples can be retrieved from the RAM in order to sent them back
					WE := '1';										--make write enable high because from this point onwards audio samples will be received that need to be written to the RAM block
				END IF;
			ELSIF c = 6 THEN 											--next 2 bytes
				IF i = 0 THEN 
					zeros_original := TO_INTEGER(UNSIGNED(converted)); 					--number of zero crossings of the original music have been received and are stored in zeros_original
				END IF;
				MISO <= autotuned(15-i);									--start sending audio samples back
			ELSIF c = 8 THEN											--next 2 bytes
				IF i = 0 THEN 											--determine initial amplitude sign
					IF TO_INTEGER(SIGNED(converted)) > 0 THEN amplitudesign := '1'; 
					ELSE amplitudesign := '0'; END IF;
				END IF;
				MISO <= autotuned(15-i);									--sent next audio sample
			ELSIF c > 8 THEN											--remaining length of the packet
				IF i = 0 THEN 											--determine if the current data has crossed zero
					IF TO_INTEGER(SIGNED(converted)) < -marge_switch AND amplitudesign = '1' THEN 		--if the previous sample is bigger than +marge_switch and the current sample is smaller than -marge_switch
						zerocrossings := zerocrossings + 1;						--increment number of zero crossings
						amplitudesign := '0';								--current sample was smaller then -marge_switch, so amplitudesign is 0
						std_zero := STD_LOGIC_VECTOR(TO_UNSIGNED(zerocrossings, 16));
					ELSIF TO_INTEGER(SIGNED(converted)) > marge_switch AND amplitudesign = '0' THEN		--if previous sample was smaller than -marge_switch and current sample is bigger than +marge_switch
						zerocrossings := zerocrossings + 1;						--also increment number of zero crossings
						amplitudesign := '1';								--since current sample is bigger than +marge_switch, make amplitude sign 1
						std_zero := STD_LOGIC_VECTOR(TO_UNSIGNED(zerocrossings, 16));
					END IF;
				END IF;
				MISO <= autotuned(15-i);									--keep sending audio samples
			END IF;
			IF i = 0 AND RE = '1' THEN										--seperate statements so the RAM block is recognized as memory bits instead of ALM's. 
				IF write_high = '0' THEN 									--either read from the top of the memory or the bottom
					autotuned := ram_block(2048 + read_address/100);					--read audio sample in autotuned so they can be sent back
				ELSE autotuned := ram_block(read_address/100); END IF;
			END IF;
				

		ELSIF SCLK'event AND SCLK = '0' THEN 										--falling edge of the clock
			IF i = 0 AND WE = '1' THEN 										--seperate statements to write to the RAM as well, because otherwise Quartus would not recognize the usage of memory blocks. 
				IF write_high = '0' THEN ram_block(write_address) <= code;					--write to either the top or bottom half of the memory
				ELSE ram_block(2048 + write_address) <= code; END IF;
				write_address := write_address + 1;								--increment the address to write to by one every clock cycle					
				IF previous_zeros_original > 5 AND previous_zeros > 0 THEN
					read_address := read_address + Kees;							--by increment in read address based on the number of zero crossings, the frequency can be altered because
				ELSE read_address := read_address + 100; END IF;
			END IF;													--if a higher frequency is required, some samples will be skipped whereas if a smaller frequency is desired, some samples will be sent multiple times.
			IF write_address = nr_of_bytes/2 THEN write_address := 0; END IF;					--reset write address when all samples have been read
			IF read_address >= 50*nr_of_bytes THEN read_address := 0; END IF;					--reset read address when all samples have been sent, this allows for resending samples if the frequency must be increased
			IF c = 0 THEN write_address := 0; read_address := 0; END IF;						--reset both if the packet has stopped
		END IF;
	END PROCESS;
END;