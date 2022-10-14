// User defined class node
class BlockNode {
    // constructor
    constructor(data) {
        this.opcode = data.opcode;
        this.inputs = data.inputs;
        this.fields = data.fields;
        this.next = null;
    }

    getBlock() {
        const inputs = [];
        for (const item in this.inputs) {
            inputs.push(this.inputs[item].getInputBlock());
        }
        const next = (this.next === null ? {} : this.next.getBlock());
        return {
            opcode: this.opcode,
            inputs: inputs,
            fields: this.fields,
            next: next
        };
    }
    precision(a) {
        if (!isFinite(a)) return 0;
        var e = 1, p = 0;
        while (Math.round(a * e) / e !== a) { e *= 10; p++; }
        return p;
    }
    verifyCode(json) {
        const isOpcodeCorrect = (json.opcode === this.opcode);
        let areFieldsCorrect = true;
        for (const field of Object.keys(this.fields)) {
            let doesFieldExist;
            try {
                doesFieldExist = (this.fields[field].name === json.fields[field].name);
            } catch (err) {
                doesFieldExist = false;
            }
            let isValueInRange;
            try {
                if (!isNaN(json.fields[field].value)) {
                    if (parseFloat(toString(this.fields[field].value)).toFixed(this.precision(parseFloat(toString(json.fields[field].value)))) === parseFloat(toString(json.fields[field].value)).toFixed(this.precision(parseFloat(toString(json.fields[field].value))))) {
                        isValueInRange = true;
                    } else {
                        isValueInRange = false;
                    }
                } else {
                    isValueInRange = (this.fields[field].value === json.fields[field].value);
                }
                // isValueInRange = (this.fields[field].value === json.fields[field].value);
                if (json.fields[field].range !== null && json.fields[field].name === 'NUM') {
                    isValueInRange = ((json.fields[field].range.left <= Number(this.fields[field].value)) && (json.fields[field].range.right >= Number(this.fields[field].value)))
                }
            } catch (err) {
                isValueInRange = false;
            }
            if (!doesFieldExist) {
                // console.log(`Field ${json.fields[field].name} does not exist`);
                areFieldsCorrect = false;
                break;
            }

            if (!isValueInRange) {
                // console.log(`Field value mismatch ${this.fields[field].value}, ${json.fields[field].value} does not match`);
                areFieldsCorrect = false;
                break;
            }
        }
        if (!isOpcodeCorrect) {
            // console.log(`${this.opcode} does not exist in sequence`);
            return false;
        }
        if (!areFieldsCorrect) {
            // console.log('fields are incorrect');
            return false;
        }
        let areInputsCorrect = true;
        for (const input in this.inputs) {
            if (!this.inputs[input].verifyCode(json.inputs[input])) {
                areInputsCorrect = false;
                break;
            }
        }
        if (!areInputsCorrect) {
            // console.log('inputs are incorrect');
            return false;
        }
        if (this.next !== null && isOpcodeCorrect && areFieldsCorrect && areInputsCorrect) {
            return this.next.verifyCode(json.next);
        }
        // console.log(json);
        return true;
    }
}

class BlockInput {
    // constructor
    constructor(data) {
        this.name = data.name;
        this.blockNode = new BlockNode(data.blockNode);
    }

    getInputBlock() {
        return {
            name: this.name,
            block: this.blockNode.getBlock()
        };
    }

    verifyCode(json) {
        const isNameCorrect = json.name === this.name;
        return this.blockNode.verifyCode(json.block);
    }
}

module.exports = {
    BlockNode,
    BlockInput
};
