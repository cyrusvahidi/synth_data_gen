s.boot;

thisProcess.platform.recordingsDir = "/Users/cyrus/Documents/AIM/projects/data/";

(
SynthDef(\synth, {
	|out=0, f0=220,
	atk=0.01, dcy=1, sustain=0, sustain_lvl=0.5, rls=1,
	cutoff=1000,
	mix=0,
	lfo_rate=0.5, use_lfo=1, lfo_range=#[0, 20000],
	f_env_levels=#[0, 20000, 0], f_env_times=#[1,1]|
	var osc1, osc2, f_lfo, f_env, f_mod, env;

	// oscillators
	osc1 = Pulse.ar(f0, mul: 1 - mix);
    osc2 = Saw.ar(f0, mix);

	// filter modulation sources
	f_lfo = SinOsc.kr(freq: lfo_rate).range(lfo_range[0], lfo_range[1]);
	f_env = EnvGen.ar(Env.new([f_env_levels[0], f_env_levels[1], f_env_levels[2]], [f_env_times[0], f_env_times[1]], [4, -4, -4, -4]), doneAction: Done.freeSelf);
	// choose filter modulation source via flag
	f_mod = if (use_lfo, f_lfo, f_env);

	env = EnvGen.ar(Env.new([0, 1, sustain_lvl, sustain_lvl, 0], [atk, dcy, sustain, rls], [4, -4, -4, -4]), doneAction: Done.freeSelf);
	// EnvGen.ar(Env.adsr(atk, dcy, 0, rls, curve: 2), doneAction: Done.freeSelf)
	x = LPF.ar(osc1 + osc2, f_mod) * env;

	Out.ar(out, x!2);
}).add;
)

Env([0, 1, 0.5, 0.5, 0], [10, 1, 1, 1], 4).plot

// Env([0, 1, 0.5, 0.5, 0], [0.5, 1, 0, 1], -4).plot
// Env([0, 20000, 0], [0.05, 1, 0.5], -4).plot // fenv


a = Synth(\synth, [\out, 0, \atk, 0, \dcy, 2, \rls, 0, \cutoff, 5000, \lfo_rate, 0.5, \mix, 0.5]);

(
var envs = List[List[0, 1, 1], List[0, 0.5, 1.5], List[0.5, 1, 0.5], List[1, 0.8, 0.2]];
var lfo_rates = List[0.5, 2];
var filter_envs = List[List[0, 5000, 0], List[0, 10000, 0]];
var mixes = List[0];
var use_lfo = List[1, 0];
var path;

f = Routine({
	envs.do({arg env;
			use_lfo.do({arg flag;
				if (flag == 1,
				{
					lfo_rates.do({arg rate;
						Synth(\synth, [
							\atk, env[0], \dcy, env[1], \rls, env[2],
							\lfo_rate, rate, \use_lfo, flag
						]);
						path = "atk_" ++ env[0] + "dcy_" ++ env[1] + "lfo_" ++ rate ++ '.wav';
						path = thisProcess.platform.recordingsDir + path;
						s.record(path: path, duration: 2);
						2.05.wait;
					})
				},
				{
					filter_envs.do({arg f_env;
						Synth(\synth, [
							\atk, env[0], \dcy, env[1], \rls, env[2],
							\use_lfo, flag,
							\f_start, f_env[0], \f_max, f_env[1], \f_end, f_env[2],
						]);
						path = "atk_" ++ env[0] + "dcy_" ++ env[1] + "fenv_s_" ++ f_env[0] + "fenv_e_" ++ f_env[1] ++ '.wav';
						path = thisProcess.platform.recordingsDir + path;
						s.record(path: path, duration: 2);
						2.05.wait;
					})
				}
				);
			});
	});
}).play;

)

// Audition
(
var env = #[0.01, 1.5, 0, 0.5, 0.5]; // atk, dcy, rls, sustain, sustain_level
var flag = 0; // 1 == lfo
var lfo_rate = 0.5;
var lfo_range = #[0, 20000];
var f_env_levels = #[0, 1000, 0];
var f_env_times = #[0.5, 1.5];
Synth(\synth, [
	\atk, env[0], \dcy, env[1], \rls, env[3], \sustain, env[2], \sustain_level, env[4],
	\use_lfo, flag, \lfo_rate, lfo_rate, \lfo_range, lfo_range,
	\f_env_levels, f_env_levels, \f_env_times, f_env_times
]);
)


// 1 - slightly low passed and subtle modulation, balanced envelope
var env = #[0.05, 0.5, 0.5, 0.5, 0.5]; // atk, dcy, rls, sustain, sustain_level
var flag = 1; // 1 == lfo
var lfo_rate = 0.5;
var lfo_range = #[0, 4000];
var f_env_levels = #[0, 1000, 0];
var f_env_times = #[1.5,0.5];
// 2 - harder low passed and subtle modulation, balanced envelope
var env = #[0.05, 0.5, 0.5, 0.5, 0.5]; // atk, dcy, rls, sustain, sustain_level
var flag = 1; // 1 == lfo
var lfo_rate = 0.5;
var lfo_range = #[30, 1000];
var f_env_levels = #[0, 1000, 0];
var f_env_times = #[1.5,0.5];
// 3 - lowpassed, longer attack, slow mod
var env = #[1, 0.5, 0.5, 0.5, 0]; // atk, dcy, rls, sustain, sustain_level
var flag = 1; // 1 == lfo
var lfo_rate = 0.5;
var lfo_range = #[0, 10000];
var f_env_levels = #[0, 1000, 0];
var f_env_times = #[1.5,0.5];
// 4 - fast mod lfo, fast attack
var env = #[0.01, 2, 0.5, 0.5, 0]; // atk, dcy, rls, sustain, sustain_level
var flag = 1; // 1 == lfo
var lfo_rate = 8;
var lfo_range = #[0, 10000];
var f_env_levels = #[0, 1000, 0];
var f_env_times = #[1.5,0.5];
// 5 - medium lfo, full range, fast attack
var env = #[0.01, 1, 0.3, 0.7, 0.5]; // atk, dcy, rls, sustain, sustain_level
var flag = 1; // 1 == lfo
var lfo_rate = 5;
var lfo_range = #[0, 20000];
var f_env_levels = #[0, 1000, 0];
var f_env_times = #[1.5,0.5];
// 6 - fast lfo, full-range, long attack
var env = #[1.5, 0.4, 0, 0.1, 0.5]; // atk, dcy, rls, sustain, sustain_level
var flag = 1; // 1 == lfo
var lfo_rate = 8;
var lfo_range = #[0, 20000];
var f_env_levels = #[0, 1000, 0];
var f_env_times = #[1.5,0.5];

// FILTER ENV
// 7 - long opening filter envelope
var env = #[0.01, 1, 0.5, 0.5, 0.5]; // atk, dcy, rls, sustain, sustain_level
var flag = 0; // 1 == lfo
var lfo_rate = 0.5;
var lfo_range = #[0, 20000];
var f_env_levels = #[0, 4000, 0];
var f_env_times = #[1.5,0.5];

// 8 - SHORT PERC AND FILTER SWEEP UP
var env = #[0.01, 0.1, 0, 0.5, 0.5]; // atk, dcy, rls, sustain, sustain_level
var flag = 0; // 1 == lfo
var lfo_rate = 0.5;
var lfo_range = #[0, 20000];
var f_env_levels = #[0, 10000, 0];
var f_env_times = #[0.1, 0.1];

// 9 - longer percy sweep up filter
var env = #[0.01, 0.5, 0, 0.5, 0.5]; // atk, dcy, rls, sustain, sustain_level
var flag = 0; // 1 == lfo
var lfo_rate = 0.5;
var lfo_range = #[0, 20000];
var f_env_levels = #[0, 20000, 0];
var f_env_times = #[0.1, 0.3];

// 10 -- long attack and long full filter sweep
var env = #[0.01, 1.5, 0, 0.5, 0.5]; // atk, dcy, rls, sustain, sustain_level
var flag = 0; // 1 == lfo
var lfo_rate = 0.5;
var lfo_range = #[0, 20000];
var f_env_levels = #[0, 20000, 0];
var f_env_times = #[1, 1];