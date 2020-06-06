s.boot;


(
SynthDef(\synth, {
	|out=0, f0=220,
	envelope=#[0.01, 1, 0, 1, 0.5],
	cutoff=1000,
	osc_mix=#[1, 0, 0],
	lfo_rate=0.5, f_mod_source=0, lfo_range=#[0, 20000],
	pw = 0.01,
	pwm=0,
	pwm_rate=2,
	f_env_levels=#[0, 20000, 0], f_env_times=#[1,1],
	filter=1|

	var osc1, osc2, osc3, f_lfo, f_env, f_mod, env, lfo_pwm;

	lfo_pwm = Select.kr(pwm, [pw, SinOsc.kr(pwm_rate).range(0.01, 0.99)]);

	// oscillators
	osc1 = LFPulse.ar(f0, width: lfo_pwm, mul: osc_mix[0]);

	osc2 = LFSaw.ar(f0, iphase: 0.5, mul: osc_mix[1]);
	osc3 = PinkNoise.ar(mul: osc_mix[2]);

	f_lfo = SinOsc.kr(freq: lfo_rate).range(lfo_range[0], lfo_range[1]);
	f_env = EnvGen.ar(Env.new([f_env_levels[0], f_env_levels[1], f_env_levels[2]], [f_env_times[0], f_env_times[1]], [4, -4, -4, -4]), doneAction: Done.freeSelf);

	// filter modulation sources
	f_mod = Select.kr(f_mod_source, [f_env, f_lfo, osc1.range(30, 10000)]);

	env = EnvGen.ar(Env.new([0, 1, envelope[4], envelope[4], 0], [envelope[0], envelope[1], envelope[2], envelope[3]], [4, -4, -4, -4]), doneAction: Done.freeSelf);

	x = Select.ar(filter, [RLPF.ar(osc1 + osc2 + osc3, f_mod, 0.5), RHPF.ar(osc1 + osc2 + osc3, f_mod, 0.5)])  * env;

	Out.ar(out, Limiter.ar(x!2, 1.0, 0.001));
}).add;
)


FreqScope.new(400, 200, 0, server: s);
// Audition
(
 var env = #[0.05, 0, 0.8, 0.2, 1]; // atk, dcy, rls, sustain, sustain_level
 var f_mod_source = 0; // 1 == lfo
 var osc_mix = [0, 0, 0.5];
 var pw = 0.2;
 var pwm_rate = 1;
 var pwm = 0;
 var lfo_rate = 1;
 var lfo_range = #[1000, 10000];
 var f_env_levels = #[10000, 10000, 1000];
 var f_env_times = #[0, 1];
 var filter = 0;
 Synth(\synth, [
	\osc_mix, osc_mix,
	\pwm_rate, pwm_rate,
	\pw, pw,
	\pwm, pwm,
 	\envelope, env,
 	\f_mod_source, f_mod_source, \lfo_rate, lfo_rate, \lfo_range, lfo_range,
 	\f_env_levels, f_env_levels, \f_env_times, f_env_times,
	\filter, filter
 ]);
s.scope()
)

Server.default.options.outDevice_("External Headphones");