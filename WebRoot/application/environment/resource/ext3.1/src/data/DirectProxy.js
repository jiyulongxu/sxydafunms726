/*!
 * Ext JS Library 3.1.0
 * Copyright(c) 2006-2009 Ext JS, LLC
 * licensing@extjs.com
 * http://www.extjs.com/license
 */
/**
 * @class Ext.data.DirectProxy
 * @extends Ext.data.DataProxy
 */
Ext.data.DirectProxy = function(config){
    Ext.apply(this, config);
    if(typeof this.paramOrder == 'string'){
        this.paramOrder = this.paramOrder.split(/[\s,|]/);
    }
    Ext.data.DirectProxy.superclass.constructor.call(this, config);
};

Ext.extend(Ext.data.DirectProxy, Ext.data.DataProxy, {
    /**
     * @cfg {Array/String} paramOrder Defaults to <tt>undefined</tt>. A list of params to be executed
     * server side.  Specify the params in the order in which they must be executed on the server-side
     * as either (1) an Array of String values, or (2) a String of params delimited by either whitespace,
     * comma, or pipe. For example,
     * any of the following would be acceptable:<pre><code>
paramOrder: ['param1','param2','param3']
paramOrder: 'param1 param2 param3'
paramOrder: 'param1,param2,param3'
paramOrder: 'param1|param2|param'
     </code></pre>
     */
    paramOrder: undefined,

    /**
     * @cfg {Boolean} paramsAsHash
     * Send parameters as a collection of named arguments (defaults to <tt>true</tt>). Providing a
     * <tt>{@link #paramOrder}</tt> nullifies this configuration.
     */
    paramsAsHash: true,

    /**
     * @cfg {Function} directFn
     * Function to call when executing a request.  directFn is a simple alternative to defining the api configuration-parameter
     * for Store's which will not implement a full CRUD api.
     */
    directFn : undefined,

    /**
     * DirectProxy implementation of {@link Ext.data.DataProxy#doRequest}
     * @param {String} action The crud action type (create, read, update, destroy)
     * @param {Ext.data.Record/Ext.data.Record[]} rs If action is load, rs will be null
     * @param {Object} params An object containing properties which are to be used as HTTP parameters
     * for the request to the remote server.
     * @param {Ext.data.DataReader} reader The Reader object which converts the data
     * object into a block of Ext.data.Records.
     * @param {Function} callback
     * <div class="sub-desc"><p>A function to be called after the request.
     * The <tt>callback</tt> is passed the following arguments:<ul>
     * <li><tt>r</tt> : Ext.data.Record[] The block of Ext.data.Records.</li>
     * <li><tt>options</tt>: Options object from the action request</li>
     * <li><tt>success</tt>: Boolean success indicator</li></ul></p></div>
     * @param {Object} scope The scope (<code>this</code> reference) in which the callback function is executed. Defaults to the browser window.
     * @param {Object} arg An optional argument which is passed to the callback as its second parameter.
     * @protected
     */
    doRequest : function(action, rs, params, reader, callback, scope, options) {
        var args = [],
            directFn = this.api[action] || this.directFn;

        switch (action) {
            case Ext.data.Api.actions.create:
                args.push(params.jsonData);		// <-- create(Hash)
                break;
            case Ext.data.Api.actions.read:
                // If the method has no parameters, ignore the paramOrder/paramsAsHash.
                if(directFn.directCfg.method.len > 0){
                    if(this.paramOrder){
                        for(var i = 0, len = this.paramOrder.length; i < len; i++){
                            args.push(params[this.paramOrder[i]]);
                        }
                    }else if(this.paramsAsHash){
                        args.push(params);
                    }
                }
                break;
            case Ext.data.Api.actions.update:
                args.push(params.jsonData);        // <-- update(Hash/Hash[])
                break;
            case Ext.data.Api.actions.destroy:
                args.push(params.jsonData);        // <-- destroy(Int/Int[])
                break;
        }

        var trans = {
            params : params || {},
            request: {
                callback : callback,
                scope : scope,
                arg : options
            },
            reader: reader
        };

        args.push(this.createCallback(action, rs, trans), this);
        directFn.apply(window, args);
    },

    // private
    createCallback : function(action, rs, trans) {
        return function(result, res) {
            if (!res.status) {
                // @deprecated fire loadexception
                if (action === Ext.data.Api.actions.read) {
                    this.fireEvent("loadexception", this, trans, res, null);
                }
                this.fireEvent('exception', this, 'remote', action, trans, res, null);
                trans.request.callback.call(trans.request.scope, null, trans.request.arg, false);
                return;
            }
            if (action === Ext.data.Api.actions.read) {
                this.onRead(action, trans, result, res);
            } else {
                this.onWrite(action, trans, result, res, rs);
            }
        };
    },
    /**
     * Callback for read actions
     * @param {String} action [Ext.data.Api.actions.create|read|update|destroy]
     * @param {Object} trans The request transaction object
     * @param {Object} result Data object picked out of the server-response.
     * @param {Object} res The server response
     * @protected
     */
    onRead : function(action, trans, result, res) {
        var records;
        try {
            records = trans.reader.readRecords(result);
        }
        catch (ex) {
            // @deprecated: Fire old loadexception for backwards-compat.
            this.fireEvent("loadexception", this, trans, res, ex);

            this.fireEvent('exception', this, 'response', action, trans, res, ex);
            trans.request.callback.call(trans.request.scope, null, trans.request.arg, false);
            return;
        }
        this.fireEvent("load", this, res, trans.request.arg);
        trans.request.callback.call(trans.request.scope, records, trans.request.arg, true);
    },
    /**
     * Callback for write actions
     * @param {String} action [{@link Ext.data.Api#actions create|read|update|destroy}]
     * @param {Object} trans The request transaction object
     * @param {Object} result Data object picked out of the server-response.
     * @param {Object} res The server response
     * @param {Ext.data.Record/[Ext.data.Record]} rs The Store resultset associated with the action.
     * @protected
     */
    onWrite : function(action, trans, result, res, rs) {
        var data = trans.reader.extractData(result, false);
        this.fireEvent("write", this, action, data, res, rs, trans.request.arg);
        trans.request.callback.call(trans.request.scope, data, res, true);
    }
});

