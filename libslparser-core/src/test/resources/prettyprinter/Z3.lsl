library Z3;
imports {
    z3.h;
}

types {
    Z3_config (Z3_config);
    Z3_context (Z3_context);
    Z3_solver (Z3_solver);
    Z3_sort (Z3_sort);
    Z3_symbol (Z3_symbol);
    Z3_ast (Z3_ast);
    Z3_solver (Z3_solver);
    Z3_lbool (Z3_lbool);
    Z3_model (Z3_model);
    Z3_error_handler (Z3_error_handler);
    Z3_func_decl (Z3_func_decl);
    Z3_ast_vector (Z3_ast_vector);
}

converters {
    Z3_ast_size <- sizeof(Z3_ast);
}

automaton Z3_config {
    state Created, Constructed, Closed;
    shift Created -> Constructed (Z3_mk_config);
    shift Constructed -> self (Z3_mk_context, Z3_set_param_value);
    shift Constructed -> Closed (Z3_del_config);
}

fun Z3_config.Z3_mk_config(): Z3_config;
fun Z3_mk_context(@handle cfg: Z3_config): Z3_context;
fun Z3_del_config(@handle cfg: Z3_config);
fun Z3_set_param_value(@handle cfg: Z3_config, param_id: Char[], param_value: Char[]);

automaton Z3_context {
    state Constructed;
    shift Constructed -> self (Z3_mk_bool_sort);
    shift Constructed -> self (Z3_mk_int_symbol, Z3_mk_const, Z3_mk_and, Z3_mk_or, Z3_mk_not, Z3_mk_iff);
    shift Constructed -> self (Z3_mk_solver, Z3_solver_inc_ref, Z3_solver_dec_ref);
    shift Constructed -> self (Z3_solver_assert, Z3_solver_check, Z3_solver_check_assumptions, Z3_solver_get_unsat_core);
    shift Constructed -> self (Z3_mk_string_symbol, Z3_mk_int_sort, Z3_solver_get_model, Z3_model_inc_ref, Z3_model_to_string, Z3_model_dec_ref, Z3_mk_add, Z3_mk_lt, Z3_mk_gt, Z3_mk_eq, Z3_mk_int, Z3_set_error_handler, Z3_get_symbol_kind, Z3_get_symbol_int, Z3_get_symbol_string, Z3_model_get_num_consts, Z3_model_get_const_decl, Z3_get_decl_name, Z3_mk_app);
    shift Constructed -> Closed (Z3_del_context);
}

fun Z3_mk_bool_sort(@handle ctx: Z3_context): Z3_sort;
fun Z3_mk_int_symbol(@handle ctx: Z3_context, i: Int): Z3_symbol;
fun Z3_mk_const(@handle ctx: Z3_context, s: Z3_symbol, ty: Z3_sort): Z3_ast;
fun Z3_mk_int(@handle c: Z3_context, v: Int, ty: Z3_sort): Z3_ast;
fun Z3_mk_not(@handle ctx: Z3_context, a: Z3_ast): Z3_ast;
fun Z3_mk_iff(@handle ctx: Z3_context, t1: Z3_ast, t2: Z3_ast): Z3_ast;
fun Z3_mk_iff(@handle ctx: Z3_context, t1: Z3_ast, t2: Z3_ast): Z3_ast;
fun Z3_mk_lt(@handle c: Z3_context, t1: Z3_ast, t2: Z3_ast): Z3_ast;
fun Z3_mk_gt(@handle c: Z3_context, t1: Z3_ast, t2: Z3_ast): Z3_ast;
fun Z3_mk_eq(@handle c: Z3_context, l: Z3_ast, r: Z3_ast): Z3_ast;
fun Z3_mk_and(@handle ctx: Z3_context, num_args: Int, args: Z3_ast[]): Z3_ast;
fun Z3_mk_or(@handle ctx: Z3_context, num_args: Int, args: Z3_ast[]): Z3_ast;
fun Z3_mk_add(@handle c: Z3_context, num_args: Int, args: Z3_ast[]): Z3_ast;
fun Z3_mk_solver(@handle ctx: Z3_context): Z3_solver;
fun Z3_solver_inc_ref(@handle ctx: Z3_context, s: Z3_solver);
fun Z3_solver_dec_ref(@handle ctx: Z3_context, s: Z3_solver);
fun Z3_solver_assert(@handle ctx: Z3_context, s: Z3_solver, a: Z3_ast);
fun Z3_solver_check(@handle ctx: Z3_context, s: Z3_solver): Int;
fun Z3_solver_check_assumptions(@handle c: Z3_context, s: Z3_solver, num_assumptions: Int, assumptions: Z3_ast[]): Int;
fun Z3_solver_get_unsat_core(@handle c: Z3_context, s: Z3_solver): Z3_ast_vector;
fun Z3_mk_string_symbol(@handle ctx: Z3_context, s: Char[]): Z3_symbol;
fun Z3_mk_int_sort(@handle c: Z3_context): Z3_sort;
fun Z3_solver_get_model(@handle c: Z3_context, s: Z3_solver): Z3_model;
fun Z3_model_inc_ref(@handle c: Z3_context, m: Z3_model);
fun Z3_model_dec_ref(@handle c: Z3_context, m: Z3_model);
fun Z3_set_error_handler(@handle c: Z3_context, h: Z3_error_handler);
fun Z3_model_to_string(@handle c: Z3_context, m: Z3_model): const<Char[]>;
fun Z3_model_get_num_consts(@handle c: Z3_context, m: Z3_model): Int;
fun Z3_model_get_const_decl(@handle c: Z3_context, m: Z3_model, i: Int): Z3_func_decl;
fun Z3_get_decl_name(@handle c: Z3_context, d: Z3_func_decl): Z3_symbol;
fun Z3_mk_app(@handle c: Z3_context, d: Z3_func_decl, num_args: Int, args: Z3_ast[]): Z3_ast;
fun Z3_model_eval(@handle c: Z3_context, m: Z3_model, t: Z3_ast, model_completion: Boolean, v: Z3_ast): Boolean;
fun Z3_del_context(@handle ctx: Z3_context);
fun Z3_get_symbol_kind(@handle ctx: Z3_context, s: Z3_symbol): Int;
fun Z3_get_symbol_int(@handle ctx: Z3_context, s: Z3_symbol): Int;
fun Z3_get_symbol_string(@handle ctx: Z3_context, s: Z3_symbol): const<Char[]>;

automaton Z3_sort {
    state Constructed;
}

automaton Z3_symbol {
    state Created, Constructed;
}

automaton Z3_ast {
    state Created, Constructed;
}

automaton Z3_solver {
    state Created, Constructed;
}

automaton Z3_model {
    state Created, Constructed;
}

automaton Z3_func_decl {
    state Created, Constructed;
}

automaton Z3_ast_vector {
    state Created, Constructed;
}

automaton Z3_error_handler {
    state Created, Constructed;
    shift Constructed -> self (invoke);
}

fun Z3_error_handler.invoke(c: Z3_context, e: Int);
