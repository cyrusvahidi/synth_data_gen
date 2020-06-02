s.boot;

thisProcess.platform.recordingsDir = "/Users/cyrus/Documents/AIM/projects/data/";

(
SynthDef(\synth, {
	|out=0, f0=220,
	envelope=#[0.01, 1, 0, 1, 0.5],
	atk=0.01, dcy=1, sustain=0, sustain_lvl=0.5, rls=1,
	cutoff=1000,
	mix=0,
	lfo_rate=0.5, f_mod_source=0, lfo_range=#[0, 20000],
	f_env_levels=#[0, 20000, 0], f_env_times=#[1,1]|

	var osc1, osc2, f_lfo, f_env, f_mod, env, lfo_pwm;

	lfo_pwm = SinOsc.kr(5);

	// oscillators
	osc1 = Pulse.ar(f0, width: lfo_pwm.range(0.01, 0.99), mul: 1 - mix);
    osc2 = Saw.ar(f0, mix);

	f_lfo = SinOsc.kr(freq: lfo_rate).range(lfo_range[0], lfo_range[1]);
	f_env = EnvGen.ar(Env.new([f_env_levels[0], f_env_levels[1], f_env_levels[2]], [f_env_times[0], f_env_times[1]], [4, -4, -4, -4]), doneAction: Done.freeSelf);

	// filter modulation sources
	f_mod = Select.kr(f_mod_source, [f_env, f_lfo, osc1.range(30, 8000)]);

	env = EnvGen.ar(Env.new([0, 1, envelope[4], envelope[4], 0], [envelope[0], envelope[1], envelope[2], envelope[3]], [4, -4, -4, -4]), doneAction: Done.freeSelf);

	x = RLPF.ar(osc1 + osc2, f_mod, lfo_pwm.range(0.5, 2)) * env;

	Out.ar(out, x!2);
}).add;
)

(
var envs = #[
	[0.05, 0.5, 0.5, 0.5, 0.5],
	[0.05, 0.5, 0.5, 0.5, 0.5],
	[1, 0.5, 0.5, 0.5, 0],
	[0.01, 2, 0.5, 0.5, 0],
	[0.01, 1, 0.3, 0.7, 0.5],
	[1.5, 0.4, 0, 0.1, 0.5],
	[0.01, 1, 0.5, 0.5, 0.5],
	[0.01, 0.1, 0, 0.5, 0.5],
	[0.01, 0.5, 0, 0.5, 0.5],
	[0.01, 1.5, 0, 0.5, 0.5],
	[0.01, 1.5, 0, 0.5, 0.5],
	[1.8, 0.2, 0, 0, 0.5],
	[1.8, 0.2, 0, 0, 0.5],
	[1.8, 0.2, 0, 0, 0.5]
];
var flags = #[1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0];
var lfo_rates = #[0.5, 0.5, 0.5, 8, 8, 8, 0, 0, 0, 0, 0, 2, 8, 0];
var lfo_ranges = #[
	[0, 4000],
	[30, 1000],
	[0, 10000],
	[0, 10000],
	[0, 20000],
	[0, 20000],
	[0, 0],
	[0, 0],
	[0, 0],
	[0, 0],
	[0, 0],
	[1000, 4000],
	[1000, 4000],
	[0, 0]
];
var f_env_levels = #[
	[0, 1000, 0],
	[0, 0, 0],
	[0, 0, 0],
	[0, 0, 0],
	[0, 0, 0],
	[0, 0, 0],
	[0, 4000, 0],
	[0, 10000, 0],
	[0, 20000, 0],
	[0, 20000, 0],
	[0, 1000, 0],
	[0, 0, 0],
	[0, 0, 0],
	[0, 4000, 0]
];
var f_env_times = #[
	[1, 1],
	[1, 1],
	[1, 1],
	[1, 1],
	[1, 1],
	[1, 1],
	[1.5,0.5],
	[0.1, 0.1],
	[0.1, 0.3],
	[1, 1],
	[0.5, 1.5],
	[1, 1],
	[1, 1],
	[1.6, 0.3]
];
var path;

f = Routine({
	envs.size.do({arg i;
		Synth(\synth, [
			\envelope, envs[i],
			\use_lfo, flags[i], \lfo_rate, lfo_rates[i], \lfo_range, lfo_ranges[i],
			\f_env_levels, f_env_levels[i], \f_env_times, f_env_times[i]
		]);
		path = i.asStringToBase(width: 2) ++ '.wav';
		path = thisProcess.platform.recordingsDir + path;
		s.record(path: path, duration: 2);
		2.05.wait;
	});
}).play;

)


// Audition
(
 var env = #[0.01, 0, 1, 0, 1]; // atk, dcy, rls, sustain, sustain_level
 var f_mod_source = 2; // 0 == lfo
 var mix = 0;
 var lfo_rate = 0;
 var lfo_range = #[1000, 4000];
 var f_env_levels = #[0, 4000, 0];
 var f_env_times = #[0.5, 0.3];
 Synth(\synth, [
	\mix, mix,
 	\envelope, env,
 	\f_mod_source, f_mod_source, \lfo_rate, lfo_rate, \lfo_range, lfo_range,
 	\f_env_levels, f_env_levels, \f_env_times, f_env_times
 ]);
)