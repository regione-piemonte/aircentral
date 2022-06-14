/*
 *Copyright Regione Piemonte - 2022
 *SPDX-License-Identifier: EUPL-1.2-or-later
 */
 /* ----------------------------------------------------------------------------
 * Original Author of file: Isabella Vespa
 * Purpose of file: Java interface in the same package as property files
 * 					(MessageBuncleClient.properties) that extends Messages 
 * 					and defines methods (name matches the property entries) 
 * 					that all return string.
 * Change log:
 *   2008-09-11: initial version
 * ----------------------------------------------------------------------------
 * $Id: MessageBundleClient.java,v 1.90 2015/10/19 13:22:22 pfvallosio Exp $
 * ----------------------------------------------------------------------------
 */

package it.csi.centrale.ui.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Java interface in the same package as property files
 * (MessageBuncleClient.properties) that extends Messages and defines methods
 * (name matches the property entries) that all return string.
 * 
 * @author isabella.vespa@csi.it
 * 
 */
public interface MessageBundleClient extends Messages {

	String locale();

	String lbl_language_italian();

	String lbl_language_english();

	String lbl_language_french();

	String lbl_login_error();

	String lbl_login_title();

	String button_login();

	String session_ended();

	String link_view();

	String link_conf();

	String button_back();

	String view_map_title();

	String link_map();

	String station_title();

	String operation_ok();

	String button_save();

	String call_polling();

	String call_cfg_station();

	String call_ui_station();

	String lbl_station_shortname();

	String lbl_station_name();

	String lbl_station_location();

	String lbl_station_address();

	String lbl_station_city();

	String lbl_station_province();

	String lbl_station_userNotes();

	String lbl_station_title();

	String error_station_not_found();

	String operation_error();

	String no_port_available();

	String not_free_line();

	String error_getting_line();

	String list_station_title();

	String lbl_station_new();

	String station_button_new();

	String station_list_title();

	String lbl_name();

	String lbl_status();

	String lbl_modify();

	String lbl_cancel();

	String station_button_delete();

	String station_button_modify();

	String disabled();

	String enabled();

	String button_help();

	String lbl_station_connection_info();

	String lbl_station_use_modem();

	String lbl_station_phone_number();

	String button_undo();

	String button_send_verify();

	String lbl_station_router_ip_address();

	String lbl_station_ip_address();

	String download();

	String lbl_enabled();

	String lbl_force_polling_time();

	String lbl_use_gps();

	String confirm_abandon_page();

	String connection_error();

	String station_uuid_error();

	String downloading_ok();

	String lbl_min_polling_date();

	String error_parsing_date();

	String conn_fields_empty();

	String error_parsing_integer();

	String lbl_cop_info();

	String polling_title();

	String extra_polling_title();

	String thresholds_temperature_title();

	String polling_time();

	String use_polling_extra();

	String download_sample_data_option();

	String all_data();

	String calibration_data();

	String office_opening_time();

	String office_close_time();

	String close_saturday();

	String close_sunday();

	String extra_office_time();

	String min_threshold_temperature();

	String max_threshold_temperature();

	String note_for_download_option();

	String max_available_lines();

	String total_num_modem();

	String num_shared_lines();

	String reserved_line();

	String max_alarm_threshold_temperature();

	String lbl_download_sample_data_enabled();

	String polling_time_empty();

	String max_num_lines_empty();

	String total_num_modem_empty();

	String num_modem_shared_lines_empty();

	String hour_value_incorrect();

	String view_station_data();

	String choose_station();

	String station_button_choose();

	String link_view_data();

	String link_sample_history();

	String link_mean_history();

	String analyzer();

	String value();

	String flag();

	String data();

	String multiple_flag();

	String periodicity();

	String button_refresh();

	String shortname_empty();

	String version_error();

	String view_station_detailed_status();

	String station_detailed_status();

	String analyzers_status_title();

	String analyzers_status_fault_title();

	String analyzers_status_id();

	String analyzers_status_brand_model();

	String analyzers_status_fault();

	String analyzers_status_maintenance();

	String analyzers_status_calibration_manual();

	String analyzers_status_autocalibration();

	String analyzers_status_autocalibration_failure();

	String station_status_title();

	String station_status_alarm_title();

	String station_status_alarm_id();

	String station_status_alarm_description();

	String station_status_alarm_value();

	String station_status_alarm_timestamp();

	String informatic_status_title();

	String not_enabled();

	String alarm();

	String ok();

	String analyzer_status_link_history();

	String station_status_link_history();

	String warning();

	String informatic_status_application();

	String informatic_board_title();

	String informatic_configuration_title();

	String informatic_file_system_status();

	String informatic_gps_status();

	String informatic_global_status();

	String no_info();

	String informatic_boardManagerInitStatus();

	String informatic_configured_initializedBoardsNumber();

	String informatic_loadConfigurationStatus();

	String informatic_saveNewConfigurationStatus();

	String informatic_acquisitionStarted();

	String gps_not_installed();

	String gps_installed();

	String gps_2d_fix();

	String gps_3d_fix();

	String gps_app_error();

	String gps_read_error();

	String gps_no_fix();

	String gps_fix();

	String gps_data();

	String gps_altitude();

	String gps_latitude();

	String gps_longitude();

	String warning_high();

	String warning_low();

	String alarm_high();

	String alarm_low();

	String lbl_table();

	String lbl_csv();

	String chart_title();

	String avg_period();

	String download_alarm();

	String notes();

	String polling_immediate_title();

	String lbl_day();

	String lbl_half_day();

	String lbl_start_date();

	String lbl_start_hour();

	String lbl_end_date();

	String lbl_end_hour();

	String lbl_show_minmax();

	String lbl_field_separator();

	String lbl_decimal_separator();

	String chart();

	String lbl_avg_periods();

	String means_data();

	String sample_data();

	String analyzer_alarm_data();

	String station_alarm_data();

	String real_time_data();

	String no_data();

	String not_valid();

	String valid();

	String error_open_chart();

	String real_time_title();

	String real_time_link_history();

	String real_time_link_means();

	String real_time_analyzer();

	String last_istant_value();

	String msg_chart_not_implemented();

	String lbl_vect_speed();

	String lbl_vect_dir();

	String lbl_dev_std();

	String lbl_scalar_speed();

	String lbl_gust_dir();

	String lbl_gust_speed();

	String lbl_calm_percent();

	String lbl_is_calm();

	String yes();

	String no();

	String informatic_dpa_status();

	String informatic_dpa_number();

	String informatic_board_failed_initialize();

	String activation();

	String active();

	String not_active();

	String url();

	String informatic_error_number();

	String informatic_error_number_application();

	String lbl_language_german();

	String lbl_station_ip_port();

	String lbl_alarm_type();

	String unexpected_server_error();

	String confirm_reload_page();

	String link_station_cfg();

	String link_map_cfg();

	String link_cop_cfg();

	String link_view_alarm();

	String router_timeout();

	String router_try_timeout();

	String router_timeout_empty();

	String router_try_timeout_empty();

	String cop_ip();

	String cop_ip_empty();

	String vectorial_speed();

	String vectorial_direction();

	String standard_deviation();

	String scalar_speed();

	String gust_speed();

	String gust_direction();

	String calm_number_percent();

	String calm();

	String lbl_wind_label();

	String chart_not_available();

	String link_db_association();

	String link_db_title();

	String unlink_db_title();

	String unlink_station_db_title();

	String link_station_database_title();

	String unlink_sensor_db_title();

	String link_sensor_database_title();

	String locale_db();

	String external_db();

	String button_match();

	String linked_station_database_title();

	String select_local_station();

	String select_external_station();

	String delete_link_station(String localStation, String externalStation);

	String link_removed();

	String sensor_confirm(String localSensor, String externalSensor);

	String link_load_commoncfg();

	String lbl_physical_dimension();

	String lbl_parameter();

	String lbl_measure_unit();

	String lbl_alarm_name();

	String lbl_other_common_cfg();

	String common_cfg_title();

	String parameter();

	String alarm_name();

	String dummy_sensor();

	String no_match();

	String new_physical_dimension();

	String lbl_new();

	String physical_dimension_title();

	String delete();

	String deleted_physical_dimension();

	String added_physical_dimension();

	String new_alarm_name();

	String new_parameter();

	String new_measure_unit();

	String param_id();

	String param_name();

	String type();

	String molecular_weight();

	String physical_dim();

	String parameter_config();

	String deleted_parameter();

	String alarm_name_title();

	String parameter_title();

	String measure_unit_title();

	String parameter_inserted();

	String parameter_updated();

	String measure_name();

	String description();

	String measure_config();

	String measure_inserted();

	String measure_updated();

	String deleted_measure_unit();

	String deleted_alarm_name();

	String alarm_id();

	String alarm_name_lbl();

	String data_quality_relevant();

	String relevant();

	String not_relevant();

	String alarm_inserted();

	String alarm_updated();

	String new_avg_period();

	String lbl_avg_period();

	String lbl_default_avg_period();

	String default_avg_period();

	String added_avg_period();

	String change_default_avg_period();

	String updated_default_avg_period();

	String lbl_storage_manager();

	String max_days_of_data();

	String max_days_of_aggregate_data();

	String disk_full_warning_threshold_percent();

	String disk_full_alarm_threshold_percent();

	String storage_manager_updated();

	String only_positive();

	String reference_temperature_K();

	String referencePressure_kPa();

	String lbl_standard();

	String standard_updated();

	String manual_operations_auto_reset_period();

	String data_write_to_disk_period();

	String door_alarm_id();

	String lbl_other();

	String other_updated();

	String cop_service_port();

	String maps_site_url_formatter();

	String conversion_addendum();

	String allowed_for_analyzer();

	String conversion_multiplyer();

	String allowed_for_acquisition();

	String conversion_formula();

	String none();

	String insert_physical_dimension();

	String mandatory();

	String write_avg_period();

	String chemical();

	String meteo();

	String other();

	String rain();

	String wind();

	String wind_dir();

	String wind_vel();

	String deleted_avg_period();

	String cop_router_ip();

	String modem_title();

	String lbl_modem_conf();

	String link_modem_conf();

	String lbl_modem();

	String new_modem();

	String deleted_modem();

	String device_id();

	String shared_line();

	String phone_prefix();

	String modem_inserted();

	String modem_updated();

	String device_id_mandatory();

	String during_polling();

	String informatic_communication_status();

	String no_diagnostic();

	String sw_crashed();

	String hw_crashed();

	String remote_router_ko();

	String local_router_ko();

	String mandatory_check();

	String last_comm_ok();

	String polling_ok();

	String consistency_error();

	String unparsable();

	String save_error();

	String load_error();

	String incompatible();

	String config_start_error();

	String config_load_error();

	String config_ok();

	String no_common_config();

	String connection_refused();

	String get_xml();

	String no_station_port();

	String no_station_conf();

	String lbl_station_polling_info();

	String lbl_warnig_download_sample_data();

	String day_format();

	String minute_format();

	String lbl_insert_shortname();

	String cop_name();

	String lbl_cop();

	String smart();

	String raid();

	String not_available();

	String extra_info();

	String consistency_error_from_cop();

	String unparsable_from_cop();

	String save_error_from_cop();

	String load_error_from_cop();

	String incompatible_from_cop();

	String config_start_error_from_cop();

	String config_load_error_from_cop();

	String config_ok_from_cop();

	String no_cmm_cfg_from_cop();

	String data_future_no_info();

	String data_in_future();

	String data_not_future();

	String lbl_local_acces_title();

	String username();

	String password();

	String map_name();

	String not_create_station();

	String lbl_virtual_cop();

	String select_virtual_cop();

	String time_host_proxy();

	String time_host_router();

	String time_host_modem();

	String time_host_lan();

	String num_reserved_lines_ui();

	String disk();

	String protocol_error();

	String polling_error();

	String unexpected_Error();

	String proxy_host();

	String proxy_port();

	String proxy_exclusion();

	String mandatory_proxy();

	String select_hour();

	String select_minutes();

	String disabled_station();

	String sure_delete_station();

	String button_refresh_removed();

	String drv_configs_locally_changed();

}
